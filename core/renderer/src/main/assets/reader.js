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
            const img = e.target.closest('img, svg, .diagram-container');
            if (!img) return;

            let src = '';
            let alt = '';

            if (img.tagName.toLowerCase() === 'img') {
                src = img.currentSrc || img.src || img.getAttribute('src') || '';
                alt = img.alt || img.title || '';
            } else if (img.tagName.toLowerCase() === 'svg' || img.classList.contains('diagram-container')) {
                const svgElem = img.tagName.toLowerCase() === 'svg' ? img : img.querySelector('svg');
                if (svgElem) {
                    const serializer = new XMLSerializer();
                    const svgString = serializer.serializeToString(svgElem);
                    src = 'data:image/svg+xml;utf8,' + encodeURIComponent(svgString);
                    alt = svgElem.getAttribute('aria-label') || 'Diagram';
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
            const doc = document.documentElement;
            const body = document.body;
            const scrollHeight = (doc.scrollHeight || body.scrollHeight) - doc.clientHeight;
            const target = Math.max(0, scrollHeight * percent);
            window.scrollTo({ top: target, behavior: 'smooth' });
        },
        setFontSize: function (sizePx) {
            document.documentElement.style.setProperty('--reader-font-size', sizePx + 'px');
            document.documentElement.style.setProperty('--reader-line-height', Math.round(sizePx * 1.55) + 'px');
        }
    };

    window.addEventListener('scroll', onScroll, { passive: true });
    window.addEventListener('DOMContentLoaded', function () {
        setupLinkInterception();
        setupImageInterception();
        extractHeadings();
    });
})();
