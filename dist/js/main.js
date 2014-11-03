goog.addDependency("base.js", ['goog'], []);
goog.addDependency("../cljs/core.js", ['cljs.core'], ['goog.string', 'goog.object', 'goog.string.StringBuffer', 'goog.array']);
goog.addDependency("../edin/drawing.js", ['edin.drawing'], ['cljs.core']);
goog.addDependency("../edin/main.js", ['edin.main'], ['goog.dom', 'cljs.core', 'edin.drawing', 'goog.events']);
goog.addDependency("../edin/buffer.js", ['edin.buffer'], ['cljs.core']);