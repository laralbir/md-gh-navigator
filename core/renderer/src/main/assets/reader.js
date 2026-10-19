/**
 * MD GitHub Navigator — WebBridge & Reader Engine
 */
(function () {
    let ticking = false;

    // Report scroll position and progress to Android
    function onScroll() {
        if (!ticking) {
            window.requestAnimationFrame(function () {
                const doc = document.documentElement;
                const body = document.body;
                const scrollTop = doc.scrollTop || body.scrollTop;
                const scrollHeight = (doc.scrollHeight || body.scrollHeight) - doc.clientHeight;
                const percent = scrollHeight > 0 ? (scrollTop / scrollHeight) : 0;

                if (window.AndroidBridge && window.AndroidBridge.onScrollProgress) {
                    window.AndroidBridge.onScrollProgress(percent, Math.round(scrollTop));
                }
                ticking = false;
            });
            ticking = true;
        }
    }

    // Intercept link clicks
    function setupLinkInterception() {
        document.addEventListener('click', function (e) {
            const target = e.target.closest('a');
            if (!target) return;

            const href = target.getAttribute('href');
            if (!href) return;

            // In-page anchor
            if (href.startsWith('#')) {
                const anchorId = href.substring(1);
                const elem = document.getElementById(anchorId) || document.querySelector(`[name="${anchorId}"]`);
                if (elem) {
                    e.preventDefault();
                    elem.scrollIntoView({ behavior: 'smooth' });
                }
                return;
            }

            // External link or relative file
            e.preventDefault();
            if (window.AndroidBridge && window.AndroidBridge.onLinkClicked) {
                window.AndroidBridge.onLinkClicked(href);
            }
        });
    }

    // Scan headings and notify TOC
    function extractHeadings() {
        const headings = document.querySelectorAll('h1, h2, h3, h4, h5, h6');
        const list = [];

        headings.forEach(function (h, index) {
            if (!h.id) {
                h.id = 'heading-' + index + '-' + encodeURIComponent(h.textContent.trim().toLowerCase().replace(/\s+/g, '-'));
            }
            list.push({
                id: h.id,
                text: h.textContent.trim(),
                level: parseInt(h.tagName.substring(1), 10)
            });
        });

        if (window.AndroidBridge && window.AndroidBridge.onHeadingsDetected) {
            window.AndroidBridge.onHeadingsDetected(JSON.stringify(list));
        }
    }

    // Intercept image and diagram clicks to open full-screen zoomable viewer
    function setupImageInterception() {
        document.addEventListener('click', function (e) {
            const img = e.target.closest('img, svg, .diagram-container, .mermaid');
            if (!img) return;

            let src = '';
            let alt = '';

            if (img.tagName.toLowerCase() === 'img') {
                src = img.currentSrc || img.src || img.getAttribute('src') || '';
                alt = img.alt || img.title || '';
            } else if (img.tagName.toLowerCase() === 'svg' || img.classList.contains('diagram-container') || img.classList.contains('mermaid')) {
                const svgElem = img.tagName.toLowerCase() === 'svg' ? img : img.querySelector('svg');
                if (svgElem) {
                    try {
                        const clone = svgElem.cloneNode(true);
                        if (!clone.getAttribute('xmlns')) {
                            clone.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
                        }
                        if (!clone.getAttribute('xmlns:xlink')) {
                            clone.setAttribute('xmlns:xlink', 'http://www.w3.org/1999/xlink');
                        }
                        const serializer = new XMLSerializer();
                        const svgString = serializer.serializeToString(clone);
                        src = 'data:image/svg+xml;base64,' + window.btoa(unescape(encodeURIComponent(svgString)));
                        alt = svgElem.getAttribute('aria-label') || 'Diagram';
                    } catch (err) {
                        const serializer = new XMLSerializer();
                        src = 'data:image/svg+xml;utf8,' + encodeURIComponent(serializer.serializeToString(svgElem));
                        alt = 'Diagram';
                    }
                }
            }

            if (src && window.AndroidBridge && window.AndroidBridge.onImageClicked) {
                e.preventDefault();
                e.stopPropagation();
                window.AndroidBridge.onImageClicked(src, alt);
            }
        }, true);
    }

    // Public API accessible from Android WebView
    window.ReaderApi = {
        scrollToHeading: function (headingId) {
            const elem = document.getElementById(headingId);
            if (elem) {
                elem.scrollIntoView({ behavior: 'smooth' });
            }
        },
        scrollToProgress: function (percent) {
            function performScroll() {
                const doc = document.documentElement;
                const body = document.body;
                const scrollHeight = (doc.scrollHeight || body.scrollHeight) - doc.clientHeight;
                if (scrollHeight > 0) {
                    const target = Math.max(0, scrollHeight * percent);
                    window.scrollTo({ top: target, behavior: 'auto' });
                }
            }
            performScroll();
            window.requestAnimationFrame(performScroll);
            setTimeout(performScroll, 80);
            setTimeout(performScroll, 200);
        },
        setFontSize: function (sizePx) {
            document.documentElement.style.setProperty('--reader-font-size', sizePx + 'px');
            document.documentElement.style.setProperty('--reader-line-height', Math.round(sizePx * 1.55) + 'px');
        }
    };

    // Wrap tables in responsive scrollable containers
    function setupScrollableTables() {
        const tables = document.querySelectorAll('table');
        tables.forEach(function (table) {
            if (table.parentElement && table.parentElement.classList.contains('table-wrapper')) {
                return;
            }
            const wrapper = document.createElement('div');
            wrapper.className = 'table-wrapper';
            table.parentNode.insertBefore(wrapper, table);
            wrapper.appendChild(table);
        });
    }

    // Convert and render Mermaid diagram code blocks
    function renderMermaidDiagrams() {
        const mermaidCodeBlocks = document.querySelectorAll('pre code.language-mermaid, pre code.language-mermaid-js, pre.mermaid');
        if (!mermaidCodeBlocks || mermaidCodeBlocks.length === 0) return;

        mermaidCodeBlocks.forEach(function (codeElem, index) {
            const preElem = codeElem.tagName.toLowerCase() === 'pre' ? codeElem : codeElem.closest('pre');
            if (!preElem) return;

            const diagramCode = codeElem.textContent.trim();
            const container = document.createElement('div');
            container.className = 'mermaid diagram-container';
            container.id = 'mermaid-diagram-' + index;
            container.textContent = diagramCode;

            preElem.parentNode.replaceChild(container, preElem);
        });

        if (window.mermaid) {
            try {
                const isDark = document.body.getAttribute('data-is-dark') === 'true';
                window.mermaid.initialize({
                    startOnLoad: false,
                    theme: isDark ? 'dark' : 'default',
                    securityLevel: 'loose'
                });
                window.mermaid.run({
                    querySelector: '.mermaid'
                });
            } catch (err) {
                console.error('Failed to render mermaid diagram:', err);
            }
        }
    }

    function initReader() {
        setupLinkInterception();
        setupImageInterception();
        extractHeadings();
        renderMermaidDiagrams();
        setupScrollableTables();
    }

    window.addEventListener('scroll', onScroll, { passive: true });
    if (document.readyState === 'loading') {
        window.addEventListener('DOMContentLoaded', initReader);
    } else {
        initReader();
    }
})();
