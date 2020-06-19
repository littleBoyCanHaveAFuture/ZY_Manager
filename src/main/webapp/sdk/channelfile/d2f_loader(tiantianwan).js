(function() {
    var errtimes = 0;

    function loadScript(url, callback) {
        var script = document.createElement("script");
        script.type = "text/javascript";

        script.onload = function() {
            callback && callback();
        };
        script.onerror = function() {
            script.parentNode.removeChild(script);
            if (errtimes < 2) {
                loadScript('//h5sdk-cdn.pagecp.com/js/jssdk/d2f.js');
            } else {
                setTimeout(function() {
                    loadScript(url);
                }, 1000);
            }
            errtimes++;
        };
        script.src = url // + '?v=' + gameVersion;
        document.getElementsByTagName("head")[0].appendChild(script);
    }
    loadScript('//h5sdk-cdn.pagecp.com/js/jssdk/d2f.js');
})();