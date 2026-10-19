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

    function safeEscapeCss(str) {
        if (!str) return '';
        if (window.CSS && window.CSS.escape) {
            return window.CSS.escape(str);
        }
        return str.replace(/([ #;&,.+*~':"!^$[\]()=>|/@])/g, '\\$1');
    }

    function generateHeadingSlug(text) {
        if (!text) return '';
        let clean = text.trim()
            .toLowerCase()
            .replace(/[\s_]+/g, '-')
            .replace(/[^\p{L}\p{N}-]/gu, '')
            .replace(/-+/g, '-')
            .replace(/^-+|-+$/g, '');
        return clean || 'section';
    }

    function removeAccents(str) {
        if (!str) return '';
        try {
            return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
        } catch (e) {
            return str;
        }
    }

    function findAnchorTarget(rawAnchor) {
        if (!rawAnchor) return null;
        let decoded = rawAnchor;
        try {
            decoded = decodeURIComponent(rawAnchor).trim();
        } catch (e) {
            decoded = rawAnchor.trim();
        }

        const slug = generateHeadingSlug(decoded);
        const foldedSlug = removeAccents(slug);

        // 1. Direct ID lookups
        let target = document.getElementById(rawAnchor) ||
                     document.getElementById(decoded) ||
                     (slug ? document.getElementById(slug) : null) ||
                     (foldedSlug ? document.getElementById(foldedSlug) : null);
        if (target) return target;

        // 2. Direct name attribute lookups (e.g. <a name="...">)
        try {
            target = document.querySelector(`[name="${safeEscapeCss(rawAnchor)}"]`) ||
                     document.querySelector(`[name="${safeEscapeCss(decoded)}"]`) ||
                     (slug ? document.querySelector(`[name="${safeEscapeCss(slug)}"]`) : null) ||
                     (foldedSlug ? document.querySelector(`[name="${safeEscapeCss(foldedSlug)}"]`) : null);
            if (target) return target;
        } catch (e) {
            // Ignore malformed selector
        }

        // 3. Match against headings (h1..h6)
        const headings = document.querySelectorAll('h1, h2, h3, h4, h5, h6');
        for (let i = 0; i < headings.length; i++) {
            const h = headings[i];
            const hText = h.textContent.trim();
            const hSlug = generateHeadingSlug(hText);
            const hFolded = removeAccents(hSlug);

            if (h.id === rawAnchor || h.id === decoded || (slug && h.id === slug) || (foldedSlug && h.id === foldedSlug)) {
                return h;
            }
            if (slug && hSlug === slug) {
                return h;
            }
            if (foldedSlug && hFolded === foldedSlug) {
                return h;
            }
            if (h.getAttribute('name') === rawAnchor || h.getAttribute('name') === decoded) {
                return h;
            }
        }

        return null;
    }

    function scrollToAnchor(rawAnchor) {
        if (!rawAnchor || rawAnchor === 'top') {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            return true;
        }
        const target = findAnchorTarget(rawAnchor);
        if (target) {
            target.scrollIntoView({ behavior: 'smooth' });
            return true;
        }
        return false;
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
                e.preventDefault();
                const rawAnchor = href.substring(1);
                scrollToAnchor(rawAnchor);
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
        const seenSlugs = {};

        headings.forEach(function (h, index) {
            if (!h.id) {
                const text = h.textContent.trim();
                const baseSlug = generateHeadingSlug(text) || ('heading-' + index);
                const count = seenSlugs[baseSlug] || 0;
                h.id = count === 0 ? baseSlug : (baseSlug + '-' + count);
                seenSlugs[baseSlug] = count + 1;
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
            scrollToAnchor(headingId);
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
