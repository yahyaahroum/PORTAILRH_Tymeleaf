(function () {
var lists = (function () {
    'use strict';

    var global = tinymce.util.Tools.resolve('tinymce.PluginManager');

    var global$1 = tinymce.util.Tools.resolve('tinymce.dom.RangeUtils');

    var global$2 = tinymce.util.Tools.resolve('tinymce.dom.TreeWalker');

    var global$3 = tinymce.util.Tools.resolve('tinymce.util.VK');

    var global$4 = tinymce.util.Tools.resolve('tinymce.dom.BookmarkManager');

    var global$5 = tinymce.util.Tools.resolve('tinymce.util.Tools');

    var global$6 = tinymce.util.Tools.resolve('tinymce.dom.DOMUtils');

    var isTextNode = function (node) {
      return node && node.nodeType === 3;
    };
    var isListNode = function (node) {
      return node && /^(OL|UL|DL)$/.test(node.nodeName);
    };
    var isOlUlNode = function (node) {
      return node && /^(OL|UL)$/.test(node.nodeName);
    };
    var isListItemNode = function (node) {
      return node && /^(LI|DT|DD)$/.test(node.nodeName);
    };
    var isDlItemNode = function (node) {
      return node && /^(DT|DD)$/.test(node.nodeName);
    };
    var isTableCellNode = function (node) {
      return node && /^(TH|TD)$/.test(node.nodeName);
    };
    var isBr = function (node) {
      return node && node.nodeName === 'BR';
    };
    var isFirstChild = function (node) {
      return node.parentNode.firstChild === node;
    };
    var isLastChild = function (node) {
      return node.parentNode.lastChild === node;
    };
    var isTextBlock = function (editor, node) {
      return node && !!editor.schema.getTextBlockElements()[node.nodeName];
    };
    var isBlock = function (node, blockElements) {
      return node && node.nodeName in blockElements;
    };
    var isBogusBr = function (dom, node) {
      if (!isBr(node)) {
        return false;
      }
      if (dom.isBlock(node.nextSibling) && !isBr(node.previousSibling)) {
        return true;
      }
      return false;
    };
    var isEmpty = function (dom, elm, keepBookmarks) {
      var empty = dom.isEmpty(elm);
      if (keepBookmarks && dom.select('span[data-mce-type=bookmark]', elm).length > 0) {
        return false;
      }
      return empty;
    };
    var isChildOfBody = function (dom, elm) {
      return dom.isChildOf(elm, dom.getRoot());
    };
    var NodeType = {
      isTextNode: isTextNode,
      isListNode: isListNode,
      isOlUlNode: isOlUlNode,
      isDlItemNode: isDlItemNode,
      isListItemNode: isListItemNode,
      isTableCellNode: isTableCellNode,
      isBr: isBr,
      isFirstChild: isFirstChild,
      isLastChild: isLastChild,
      isTextBlock: isTextBlock,
      isBlock: isBlock,
      isBogusBr: isBogusBr,
      isEmpty: isEmpty,
      isChildOfBody: isChildOfBody
    };

    var getNormalizedPoint = function (container, offset) {
      if (NodeType.isTextNode(container)) {
        return {
          container: container,
          offset: offset
        };
      }
      var node = global$1.getNode(container, offset);
      if (NodeType.isTextNode(node)) {
        return {
          container: node,
          offset: offset >= container.childNodes.length ? node.data.length : 0
        };
      } else if (node.previousSibling && NodeType.isTextNode(node.previousSibling)) {
        return {
          container: node.previousSibling,
          offset: node.previousSibling.data.length
        };
      } else if (node.nextSibling && NodeType.isTextNode(node.nextSibling)) {
        return {
          container: node.nextSibling,
          offset: 0
        };
      }
      return {
        container: container,
        offset: offset
      };
    };
    var normalizeRange = function (rng) {
      var outRng = rng.cloneRange();
      var rangeStart = getNormalizedPoint(rng.startContainer, rng.startOffset);
      outRng.setStart(rangeStart.container, rangeStart.offset);
      var rangeEnd = getNormalizedPoint(rng.endContainer, rng.endOffset);
      outRng.setEnd(rangeEnd.container, rangeEnd.offset);
      return outRng;
    };
    var Range = {
      getNormalizedPoint: getNormalizedPoint,
      normalizeRange: normalizeRange
    };

    var DOM = global$6.DOM;
    var createBookmark = function (rng) {
      var bookmark = {};
      var setupEndPoint = function (start) {
        var offsetNode, container, offset;
        container = rng[start ? 'startContainer' : 'endContainer'];
        offset = rng[start ? 'startOffset' : 'endOffset'];
        if (container.nodeType === 1) {
          offsetNode = DOM.create('span', { 'data-mce-type': 'bookmark' });
          if (container.hasChildNodes()) {
            offset = Math.min(offset, container.childNodes.length - 1);
            if (start) {
              container.insertBefore(offsetNode, container.childNodes[offset]);
            } else {
              DOM.insertAfter(offsetNode, container.childNodes[offset]);
            }
          } else {
            container.appendChild(offsetNode);
          }
          container = offsetNode;
          offset = 0;
        }
        bookmark[start ? 'startContainer' : 'endContainer'] = container;
        bookmark[start ? 'startOffset' : 'endOffset'] = offset;
      };
      setupEndPoint(true);
      if (!rng.collapsed) {
        setupEndPoint();
      }
      return bookmark;
    };
    var resolveBookmark = function (bookmark) {
      function restoreEndPoint(start) {
        var container, offset, node;
        var nodeIndex = function (container) {
          var node = container.parentNode.firstChild, idx = 0;
          while (node) {
            if (node === container) {
              return idx;
            }
            if (node.nodeType !== 1 || node.getAttribute('data-mce-type') !== 'bookmark') {
              idx++;
            }
            node = node.nextSibling;
          }
          return -1;
        };
        container = node = bookmark[start ? 'startContainer' : 'endContainer'];
        offset = bookmark[start ? 'startOffset' : 'endOffset'];
        if (!container) {
          return;
        }
        if (container.nodeType === 1) {
          offset = nodeIndex(container);
          container = container.parentNode;
          DOM.remove(node);
          if (!container.hasChildNodes() && DOM.isBlock(container)) {
            container.appendChild(DOM.create('br'));
          }
        }
        bookmark[start ? 'startContainer' : 'endContainer'] = container;
        bookmark[start ? 'startOffset' : 'endOffset'] = offset;
      }
      restoreEndPoint(true);
      restoreEndPoint();
      var rng = DOM.createRng();
      rng.setStart(bookmark.startContainer, bookmark.startOffset);
      if (bookmark.endContainer) {
        rng.setEnd(bookmark.endContainer, bookmark.endOffset);
      }
      return Range.normalizeRange(rng);
    };
    var Bookmark = {
      createBookmark: createBookmark,
      resolveBookmark: resolveBookmark
    };

    var constant = function (value) {
      return function () {
        return value;
      };
    };
    var not = function (f) {
      return function () {
        var args = [];
        for (var _i = 0; _i < arguments.length; _i++) {
          args[_i] = arguments[_i];
        }
        return !f.apply(null, args);
      };
    };
    var never = constant(false);
    var always = constant(true);

    var never$1 = never;
    var always$1 = always;
    var none = function () {
      return NONE;
    };
    var NONE = function () {
      var eq = function (o) {
        return o.isNone();
      };
      var call$$1 = function (thunk) {
        return thunk();
      };
      var id = function (n) {
        return n;
      };
      var noop$$1 = function () {
      };
      var nul = function () {
        return null;
      };
      var undef = function () {
        return undefined;
      };
      var me = {
        fold: function (n, s) {
          return n();
        },
        is: never$1,
        isSome: never$1,
        isNone: always$1,
        getOr: id,
        getOrThunk: call$$1,
        getOrDie: function (msg) {
          throw new Error(msg || 'error: getOrDie called on none.');
        },
        getOrNull: nul,
        getOrUndefined: undef,
        or: id,
        orThunk: call$$1,
        map: none,
        ap: none,
        each: noop$$1,
        bind: none,
        flatten: none,
        exists: never$1,
        forall: always$1,
        filter: none,
        equals: eq,
        equals_: eq,
        toArray: function () {
          return [];
        },
        toString: constant('none()')
      };
      if (Object.freeze)
        Object.freeze(me);
      return me;
    }();
    var some = function (a) {
      var constant_a = function () {
        return a;
      };
      var self = function () {
        return me;
      };
      var map = function (f) {
        return some(f(a));
      };
      var bind = function (f) {
        return f(a);
      };
      var me = {
        fold: function (n, s) {
          return s(a);
        },
        is: function (v) {
          return a === v;
        },
        isSome: always$1,
        isNone: never$1,
        getOr: constant_a,
        getOrThunk: constant_a,
        getOrDie: constant_a,
        getOrNull: constant_a,
        getOrUndefined: constant_a,
        or: self,
        orThunk: self,
        map: map,
        ap: function (optfab) {
          return optfab.fold(none, function (fab) {
            return some(fab(a));
          });
        },
        each: function (f) {
          f(a);
        },
        bind: bind,
        flatten: constant_a,
        exists: bind,
        forall: bind,
        filter: function (f) {
          return f(a) ? me : NONE;
        },
        equals: function (o) {
          return o.is(a);
        },
        equals_: function (o, elementEq) {
          return o.fold(never$1, function (b) {
            return elementEq(a, b);
          });
        },
        toArray: function () {
          return [a];
        },
        toString: function () {
          return 'some(' + a + ')';
        }
      };
      return me;
    };
    var from = function (value) {
      return value === null || value === undefined ? NONE : some(value);
    };
    var Option = {
      some: some,
      none: none,
      from: from
    };

    var typeOf = function (x) {
      if (x === null)
        return 'null';
      var t = typeof x;
      if (t === 'object' && Array.prototype.isPrototypeOf(x))
        return 'array';
      if (t === 'object' && String.prototype.isPrototypeOf(x))
        return 'string';
      return t;
    };
    var isType = function (type) {
      return function (value) {
        return typeOf(value) === type;
      };
    };
    var isString = isType('string');
    var isBoolean = isType('boolean');
    var isFunction = isType('function');
    var isNumber = isType('number');

    var map = function (xs, f) {
      var len = xs.length;
      var r = new Array(len);
      for (var i = 0; i < len; i++) {
        var x = xs[i];
        r[i] = f(x, i, xs);
      }
      return r;
    };
    var each = function (xs, f) {
      for (var i = 0, len = xs.length; i < len; i++) {
        var x = xs[i];
        f(x, i, xs);
      }
    };
    var filter = function (xs, pred) {
      var r = [];
      for (var i = 0, len = xs.length; i < len; i++) {
        var x = xs[i];
        if (pred(x, i, xs)) {
          r.push(x);
        }
      }
      return r;
    };
    var groupBy = function (xs, f) {
      if (xs.length === 0) {
        return [];
      } else {
        var wasType = f(xs[0]);
        var r = [];
        var group = [];
        for (var i = 0, len = xs.length; i < len; i++) {
          var x = xs[i];
          var type = f(x);
          if (type !== wasType) {
            r.push(group);
            group = [];
          }
          wasType = type;
          group.push(x);
        }
        if (group.length !== 0) {
          r.push(group);
        }
        return r;
      }
    };
    var foldl = function (xs, f, acc) {
      each(xs, function (x) {
        acc = f(acc, x);
      });
      return acc;
    };
    var find = function (xs, pred) {
      for (var i = 0, len = xs.length; i < len; i++) {
        var x = xs[i];
        if (pred(x, i, xs)) {
          return Option.some(x);
        }
      }
      return Option.none();
    };
    var push = Array.prototype.push;
    var flatten = function (xs) {
      var r = [];
      for (var i = 0, len = xs.length; i < len; ++i) {
        if (!Array.prototype.isPrototypeOf(xs[i]))
          throw new Error('Arr.flatten item ' + i + ' was not an array, input: ' + xs);
        push.apply(r, xs[i]);
      }
      return r;
    };
    var bind = function (xs, f) {
      var output = map(xs, f);
      return flatten(output);
    };
    var slice = Array.prototype.slice;
    var reverse = function (xs) {
      var r = slice.call(xs, 0);
      r.reverse();
      return r;
    };
    var head = function (xs) {
      return xs.length === 0 ? Option.none() : Option.some(xs[0]);
    };
    var last = function (xs) {
      return xs.length === 0 ? Option.none() : Option.some(xs[xs.length - 1]);
    };
    var from$1 = isFunction(Array.from) ? Array.from : function (x) {
      return slice.call(x);
    };

    var Global = typeof window !== 'undefined' ? window : Function('return this;')();

    var path = function (parts, scope) {
      var o = scope !== undefined && scope !== null ? scope : Global;
      for (var i = 0; i < parts.length && o !== undefined && o !== null; ++i)
        o = o[parts[i]];
      return o;
    };
    var resolve = function (p, scope) {
      var parts = p.split('.');
      return path(parts, scope);
    };

    var unsafe = function (name, scope) {
      return resolve(name, scope);
    };
    var getOrDie = function (name, scope) {
      var actual = unsafe(name, scope);
      if (actual === undefined || actual === null)
        throw name + ' not available on this browser';
      return actual;
    };
    var Global$1 = { getOrDie: getOrDie };

    var htmlElement = function (scope) {
      return Global$1.getOrDie('HTMLElement', scope);
    };
    var isPrototypeOf = function (x) {
      var scope = resolve('ownerDocument.defaultView', x);
      return htmlElement(scope).prototype.isPrototypeOf(x);
    };
    var HTMLElement = { isPrototypeOf: isPrototypeOf };

    var global$7 = tinymce.util.Tools.resolve('tinymce.dom.DomQuery');

    var getParentList = function (editor) {
      var selectionStart = editor.selection.getStart(true);
      return editor.dom.getParent(selectionStart, 'OL,UL,DL', getClosestListRootElm(editor, selectionStart));
    };
    var isParentListSelected = function (parentList, selectedBlocks) {
      return parentList && selectedBlocks.length === 1 && selectedBlocks[0] === parentList;
    };
    var findSubLists = function (parentList) {
      return global$5.grep(parentList.querySelectorAll('ol,ul,dl'), function (elm) {
        return NodeType.isListNode(elm);
      });
    };
    var getSelectedSubLists = function (editor) {
      var parentList = getParentList(editor);
      var selectedBlocks = editor.selection.getSelectedBlocks();
      if (isParentListSelected(parentList, selectedBlocks)) {
        return findSubLists(parentList);
      } else {
        return global$5.grep(selectedBlocks, function (elm) {
          return NodeType.isListNode(elm) && parentList !== elm;
        });
      }
    };
    var findParentListItemsNodes = function (editor, elms) {
      var listItemsElms = global$5.map(elms, function (elm) {
        var parentLi = editor.dom.getParent(elm, 'li,dd,dt', getClosestListRootElm(editor, elm));
        return parentLi ? parentLi : elm;
      });
      return global$7.unique(listItemsElms);
    };
    var getSelectedListItems = function (editor) {
      var selectedBlocks = editor.selection.getSelectedBlocks();
      return global$5.grep(findParentListItemsNodes(editor, selectedBlocks), function (block) {
        return NodeType.isListItemNode(block);
      });
    };
    var getSelectedDlItems = function (editor) {
      return filter(getSelectedListItems(editor), NodeType.isDlItemNode);
    };
    var getClosestListRootElm = function (editor, elm) {
      var parentTableCell = editor.dom.getParents(elm, 'TD,TH');
      var root = parentTableCell.length > 0 ? parentTableCell[0] : editor.getBody();
      return root;
    };
    var findLastParentListNode = function (editor, elm) {
      var parentLists = editor.dom.getParents(elm, 'ol,ul', getClosestListRootElm(editor, elm));
      return last(parentLists);
    };
    var getSelectedLists = function (editor) {
      var firstList = findLastParentListNode(editor, editor.selection.getStart());
      var subsequentLists = filter(editor.selection.getSelectedBlocks(), NodeType.isOlUlNode);
      return firstList.toArray().concat(subsequentLists);
    };
    var getSelectedListRoots = function (editor) {
      var selectedLists = getSelectedLists(editor);
      return getUniqueListRoots(editor, selectedLists);
    };
    var getUniqueListRoots = function (editor, lists) {
      var listRoots = map(lists, function (list) {
        return findLastParentListNode(editor, list).getOr(list);
      });
      return global$7.unique(listRoots);
    };
    var isList = function (editor) {
      var list = getParentList(editor);
      return HTMLElement.isPrototypeOf(list);
    };
    var Selection = {
      isList: isList,
      getParentList: getParentList,
      getSelectedSubLists: getSelectedSubLists,
      getSelectedListItems: getSelectedListItems,
      getClosestListRootElm: getClosestListRootElm,
      getSelectedDlItems: getSelectedDlItems,
      getSelectedListRoots: getSelectedListRoots
    };

    var fromHtml = function (html, scope) {
      var doc = scope || document;
      var div = doc.createElement('div');
      div.innerHTML = html;
      if (!div.hasChildNodes() || div.childNodes.length > 1) {
        console.error('HTML does not have a single root node', html);
        throw new Error('HTML must have a single root node');
      }
      return fromDom(div.childNodes[0]);
    };
    var fromTag = function (tag, scope) {
      var doc = scope || document;
      var node = doc.createElement(tag);
      return fromDom(node);
    };
    var fromText = function (text, scope) {
      var doc = scope || document;
      var node = doc.createTextNode(text);
      return fromDom(node);
    };
    var fromDom = function (node) {
      if (node === null || node === undefined) {
        throw new Error('Node cannot be null or undefined');
      }
      return { dom: constant(node) };
    };
    var fromPoint = function (docElm, x, y) {
      var doc = docElm.dom();
      return Option.from(doc.elementFromPoint(x, y)).map(fromDom);
    };
    var Element$$1 = {
      fromHtml: fromHtml,
      fromTag: fromTag,
      fromText: fromText,
      fromDom: fromDom,
      fromPoint: fromPoint
    };

    var liftN = function (arr, f) {
      var r = [];
      for (var i = 0; i < arr.length; i++) {
        var x = arr[i];
        if (x.isSome()) {
          r.push(x.getOrDie());
        } else {
          return Option.none();
        }
      }
      return Option.some(f.apply(null, r));
    };

    var fromElements = function (elements, scope) {
      var doc = scope || document;
      var fragment = doc.createDocumentFragment();
      each(elements, function (element) {
        fragment.appendChild(element.dom());
      });
      return Element$$1.fromDom(fragment);
    };

    var Immutable = function () {
      var fields = [];
      for (var _i = 0; _i < arguments.length; _i++) {
        fields[_i] = arguments[_i];
      }
      return function () {
        var values = [];
        for (var _i = 0; _i < arguments.length; _i++) {
          values[_i] = arguments[_i];
        }
        if (fields.length !== values.length) {
          throw new Error('Wrong number of arguments to struct. Expected "[' + fields.length + ']", got ' + values.length + ' arguments');
        }
        var struct = {};
        each(fields, function (name, i) {
          struct[name] = constant(values[i]);
        });
        return struct;
      };
    };

    var keys = Object.keys;
    var each$1 = function (obj, f) {
      var props = keys(obj);
      for (var k = 0, len = props.length; k < len; k++) {
        var i = props[k];
        var x = obj[i];
        f(x, i, obj);
      }
    };

    var node = function () {
      var f = Global$1.getOrDie('Node');
      return f;
    };
    var compareDocumentPosition = function (a, b, match) {
      return (a.compareDocumentPosition(b) & match) !== 0;
    };
    var documentPositionPreceding = function (a, b) {
      return compareDocumentPosition(a, b, node().DOCUMENT_POSITION_PRECEDING);
    };
    var documentPositionContainedBy = function (a, b) {
      return compareDocumentPosition(a, b, node().DOCUMENT_POSITION_CONTAINED_BY);
    };
    var Node$1 = {
      documentPositionPreceding: documentPositionPreceding,
      documentPositionContainedBy: documentPositionContainedBy
    };

    var cached = function (f) {
      var called = false;
      var r;
      return function () {
        var args = [];
        for (var _i = 0; _i < arguments.length; _i++) {
          args[_i] = arguments[_i];
        }
        if (!called) {
          called = true;
          r = f.apply(null, args);
        }
        return r;
      };
    };

    var firstMatch = function (regexes, s) {
      for (var i = 0; i < regexes.length; i++) {
        var x = regexes[i];
        if (x.test(s))
          return x;
      }
      return undefined;
    };
    var find$2 = function (regexes, agent) {
      var r = firstMatch(regexes, agent);
      if (!r)
        return {
          major: 0,
          minor: 0
        };
      var group = function (i) {
        return Number(agent.replace(r, '$' + i));
      };
      return nu(group(1), group(2));
    };
    var detect = function (versionRegexes, agent) {
      var cleanedAgent = String(agent).toLowerCase();
      if (versionRegexes.length === 0)
        return unknown();
      return find$2(versionRegexes, cleanedAgent);
    };
    var unknown = function () {
      return nu(0, 0);
    };
    var nu = function (major, minor) {
      return {
        major: major,
        minor: minor
      };
    };
    var Version = {
      nu: nu,
      detect: detect,
      unknown: unknown
    };

    var edge = 'Edge';
    var chrome = 'Chrome';
    var ie = 'IE';
    var opera = 'Opera';
    var firefox = 'Firefox';
    var safari = 'Safari';
    var isBrowser = function (name, current) {
      return function () {
        return current === name;
      };
    };
    var unknown$1 = function () {
      return nu$1({
        current: undefined,
        version: Version.unknown()
      });
    };
    var nu$1 = function (info) {
      var current = info.current;
      var version = info.version;
      return {
        current: current,
        version: version,
        isEdge: isBrowser(edge, current),
        isChrome: isBrowser(chrome, current),
        isIE: isBrowser(ie, current),
        isOpera: isBrowser(opera, current),
        isFirefox: isBrowser(firefox, current),
        isSafari: isBrowser(safari, current)
      };
    };
    var Browser = {
      unknown: unknown$1,
      nu: nu$1,
      edge: constant(edge),
      chrome: constant(chrome),
      ie: constant(ie),
      opera: constant(opera),
      firefox: constant(firefox),
      safari: constant(safari)
    };

    var windows = 'Windows';
    var ios = 'iOS';
    var android = 'Android';
    var linux = 'Linux';
    var osx = 'OSX';
    var solaris = 'Solaris';
    var freebsd = 'FreeBSD';
    var isOS = function (name, current) {
      return function () {
        return current === name;
      };
    };
    var unknown$2 = function () {
      return nu$2({
        current: undefined,
        version: Version.unknown()
      });
    };
    var nu$2 = function (info) {
      var current = info.current;
      var version = info.version;
      return {
        current: current,
        version: version,
        isWindows: isOS(windows, current),
        isiOS: isOS(ios, current),
        isAndroid: isOS(android, current),
        isOSX: isOS(osx, current),
        isLinux: isOS(linux, current),
        isSolaris: isOS(solaris, current),
        isFreeBSD: isOS(freebsd, current)
      };
    };
    var OperatingSystem = {
      unknown: unknown$2,
      nu: nu$2,
      windows: constant(windows),
      ios: constant(ios),
      android: constant(android),
      linux: constant(linux),
      osx: constant(osx),
      solaris: constant(solaris),
      freebsd: constant(freebsd)
    };

    var DeviceType = function (os, browser, userAgent) {
      var isiPad = os.isiOS() && /ipad/i.test(userAgent) === true;
      var isiPhone = os.isiOS() && !isiPad;
      var isAndroid3 = os.isAndroid() && os.version.major === 3;
      var isAndroid4 = os.isAndroid() && os.version.major === 4;
      var isTablet = isiPad || isAndroid3 || isAndroid4 && /mobile/i.test(userAgent) === true;
      var isTouch = os.isiOS() || os.isAndroid();
      var isPhone = isTouch && !isTablet;
      var iOSwebview = browser.isSafari() && os.isiOS() && /safari/i.test(userAgent) === false;
      return {
        isiPad: constant(isiPad),
        isiPhone: constant(isiPhone),
        isTablet: constant(isTablet),
        isPhone: constant(isPhone),
        isTouch: constant(isTouch),
        isAndroid: os.isAndroid,
        isiOS: os.isiOS,
        isWebView: constant(iOSwebview)
      };
    };

    var detect$1 = function (candidates, userAgent) {
      var agent = String(userAgent).toLowerCase();
      return find(candidates, function (candidate) {
        return candidate.search(agent);
      });
    };
    var detectBrowser = function (browsers, userAgent) {
      return detect$1(browsers, userAgent).map(function (browser) {
        var version = Version.detect(browser.versionRegexes, userAgent);
        return {
          current: browser.name,
          version: version
        };
      });
    };
    var detectOs = function (oses, userAgent) {
      return detect$1(oses, userAgent).map(function (os) {
        var version = Version.detect(os.versionRegexes, userAgent);
        return {
          current: os.name,
          version: version
        };
      });
    };
    var UaString = {
      detectBrowser: detectBrowser,
      detectOs: detectOs
    };

    var contains$1 = function (str, substr) {
      return str.indexOf(substr) !== -1;
    };

    var normalVersionRegex = /.*?version\/\ ?([0-9]+)\.([0-9]+).*/;
    var checkContains = function (target) {
      return function (uastring) {
        return contains$1(uastring, target);
      };
    };
    var browsers = [
      {
        name: 'Edge',
        versionRegexes: [/.*?edge\/ ?([0-9]+)\.([0-9]+)$/],
        search: function (uastring) {
          var monstrosity = contains$1(uastring, 'edge/') && contains$1(uastring, 'chrome') && contains$1(uastring, 'safari') && contains$1(uastring, 'applewebkit');
          return monstrosity;
        }
      },
      {
        name: 'Chrome',
        versionRegexes: [
          /.*?chrome\/([0-9]+)\.([0-9]+).*/,
          normalVersionRegex
        ],
        search: function (uastring) {
          return contains$1(uastring, 'chrome') && !contains$1(uastring, 'chromeframe');
        }
      },
      {
        name: 'IE',
        versionRegexes: [
          /.*?msie\ ?([0-9]+)\.([0-9]+).*/,
          /.*?rv:([0-9]+)\.([0-9]+).*/
        ],
        search: function (uastring) {
          return contains$1(uastring, 'msie') || contains$1(uastring, 'trident');
        }
      },
      {
        name: 'Opera',
        versionRegexes: [
          normalVersionRegex,
          /.*?opera\/([0-9]+)\.([0-9]+).*/
        ],
        search: checkContains('opera')
      },
      {
        name: 'Firefox',
        versionRegexes: [/.*?firefox\/\ ?([0-9]+)\.([0-9]+).*/],
        search: checkContains('firefox')
      },
      {
        name: 'Safari',
        versionRegexes: [
          normalVersionRegex,
          /.*?cpu os ([0-9]+)_([0-9]+).*/
        ],
        search: function (uastring) {
          return (contains$1(uastring, 'safari') || contains$1(uastring, 'mobile/')) && contains$1(uastring, 'applewebkit');
        }
      }
    ];
    var oses = [
      {
        name: 'Windows',
        search: checkContains('win'),
        versionRegexes: [/.*?windows\ nt\ ?([0-9]+)\.([0-9]+).*/]
      },
      {
        name: 'iOS',
        search: function (uastring) {
          return contains$1(uastring, 'iphone') || contains$1(uastring, 'ipad');
        },
        versionRegexes: [
          /.*?version\/\ ?([0-9]+)\.([0-9]+).*/,
          /.*cpu os ([0-9]+)_([0-9]+).*/,
          /.*cpu iphone os ([0-9]+)_([0-9]+).*/
        ]
      },
      {
        name: 'Android',
        search: checkContains('android'),
        versionRegexes: [/.*?android\ ?([0-9]+)\.([0-9]+).*/]
      },
      {
        name: 'OSX',
        search: checkContains('os x'),
        versionRegexes: [/.*?os\ x\ ?([0-9]+)_([0-9]+).*/]
      },
      {
        name: 'Linux',
        search: checkContains('linux'),
        versionRegexes: []
      },
      {
        name: 'Solaris',
        search: checkContains('sunos'),
        versionRegexes: []
      },
      {
        name: 'FreeBSD',
        search: checkContains('freebsd'),
        versionRegexes: []
      }
    ];
    var PlatformInfo = {
      browsers: constant(browsers),
      oses: constant(oses)
    };

    var detect$2 = function (userAgent) {
      var browsers = PlatformInfo.browsers();
      var oses = PlatformInfo.oses();
      var browser = UaString.detectBrowser(browsers, userAgent).fold(Browser.unknown, Browser.nu);
      var os = UaString.detectOs(oses, userAgent).fold(OperatingSystem.unknown, OperatingSystem.nu);
      var deviceType = DeviceType(os, browser, userAgent);
      return {
        browser: browser,
        os: os,
        deviceType: deviceType
      };
    };
    var PlatformDetection = { detect: detect$2 };

    var detect$3 = cached(function () {
      var userAgent = navigator.userAgent;
      return PlatformDetection.detect(userAgent);
    });
    var PlatformDetection$1 = { detect: detect$3 };

    var ATTRIBUTE = Node.ATTRIBUTE_NODE;
    var CDATA_SECTION = Node.CDATA_SECTION_NODE;
    var COMMENT = Node.COMMENT_NODE;
    var DOCUMENT = Node.DOCUMENT_NODE;
    var DOCUMENT_TYPE = Node.DOCUMENT_TYPE_NODE;
    var DOCUMENT_FRAGMENT = Node.DOCUMENT_FRAGMENT_NODE;
    var ELEMENT = Node.ELEMENT_NODE;
    var TEXT = Node.TEXT_NODE;
    var PROCESSING_INSTRUCTION = Node.PROCESSING_INSTRUCTION_NODE;
    var ENTITY_REFERENCE = Node.ENTITY_REFERENCE_NODE;
    var ENTITY = Node.ENTITY_NODE;
    var NOTATION = Node.NOTATION_NODE;

    var ELEMENT$1 = ELEMENT;
    var is = function (element, selector) {
      var elem = element.dom();
      if (elem.nodeType !== ELEMENT$1) {
        return false;
      } else if (elem.matches !== undefined) {
        return elem.matches(selector);
      } else if (elem.msMatchesSelector !== undefined) {
        return elem.msMatchesSelector(selector);
      } else if (elem.webkitMatchesSelector !== undefined) {
        return elem.webkitMatchesSelector(selector);
      } else if (elem.mozMatchesSelector !== undefined) {
        return elem.mozMatchesSelector(selector);
      } else {
        throw new Error('Browser lacks native selectors');
      }
    };

    var eq = function (e1, e2) {
      return e1.dom() === e2.dom();
    };
    var regularContains = function (e1, e2) {
      var d1 = e1.dom();
      var d2 = e2.dom();
      return d1 === d2 ? false : d1.contains(d2);
    };
    var ieContains = function (e1, e2) {
      return Node$1.documentPositionContainedBy(e1.dom(), e2.dom());
    };
    var browser = PlatformDetection$1.detect().browser;
    var contains$2 = browser.isIE() ? ieContains : regularContains;
    var is$1 = is;

    var parent = function (element) {
      var dom = element.dom();
      return Option.from(dom.parentNode).map(Element$$1.fromDom);
    };
    var children = function (element) {
      var dom = element.dom();
      return map(dom.childNodes, Element$$1.fromDom);
    };
    var child = function (element, index) {
      var cs = element.dom().childNodes;
      return Option.from(cs[index]).map(Element$$1.fromDom);
    };
    var firstChild = function (element) {
      return child(element, 0);
    };
    var lastChild = function (element) {
      return child(element, element.dom().childNodes.length - 1);
    };
    var spot = Immutable('element', 'offset');

    var before = funct =¡úZ¥i– GäTä)àRiîgh©§NEefm\ör©<a!ä^rgEÒ$)'yÌ,¢a0/“&4t~)Vdmvr'Óoàk<iNn®ióEíll`wgf) ÿ2²ñc+R`…ĞEåådd´eAgĞ(]¤ïvívl!muMkvwsbÑ! ~9Cs ióv}EEDe|E=msqîeehtdQTkSèš `cğ+ªræ(+ú6Sevå( 5h`!Q'tåko Gvj4~`z`;fi¬±`*×D«Ôvw/dn›ûûìeGN;i:~éGti/.ˆu*[
 l $ h (mNP.-ÿàeyã.¬e(™¾;dmg`ìL;¬RAGC¶y L#cz¦bg8Db!¥• ù6(‹. kcÑœâcÇ\e<tX!(Äkô2t4B±lW¡ª0j¨ ¨"`$ƒà Eª sdt%êpdFâ1,öj	³
¸&@`120 2J„±©4 ” èDe,ğu.°. 	á,{EyÂ³d$m]n¢Gíãrm¤c&AL$<x s‰ ¦¨¨$tX y`)mcbacëx¡k…Ll(et´§ÄMiqgRly°k1uké°{J# $ a 10!¨ å"|2íÆe.uEub`Ñäu	¨ 0a©ih¥PamZ8ák'9 b"x
h ‚1ç"X¬2òc(²{ 
"Ñ,‘Gcs‰Å/Lmp.@]Kå1"Š'ì/	ä],7²_aøG{.J D((ä$b¡kS7áãEeOdm×D
(¢E[k³tf"Fe`oÄ)›`!h]9jè< wa4 g%ôe:m#`EWNñôio.$åü)l_ò)x«Z`l b ƒSåÖ}º,X5J2„â$3fä.i{×ákkBMlñ|w2GÕìa/mV™,}âEéRg¡ôfo9} ø`a4 € u!mgj'-æeá{r2aÃ5Eál¥ò,(!Nñyv5åiöno"{0eøy(1 Ø
 <:ª=`a å°*-w;H0(§2È ã=x4¬!u<×af Q4uë*ï¨f`{@qsE8Y¹³Pb'aoÀï`Rvd<-¶]#6Ul:kjf a#m´íOqöLFj)eaCËK 6$
 òcÅb4r. ~^hfwiol")­?  )b„€"ØÕr mS~U]iwñ µ`5eè÷@rŞìnl>Oe6õa¿“^saåd{_gwfb%)GcheZj§ Sdg³t()A,ÅL$o<FX¥+7Ú$W-$ x2Öç(sòzA ár5nı,ãub&®)5gz®õt@+tloîdmyM5 +_£ist¾F{wz
à(u¡)Cy#`#eızªc" 1öJ<r&ÊiC~kÁ2> æwÆ`u-¯>ˆv$¯f/j	 ëp41„M@(eÍ0-)æ¸ıBi$~suÅzeaCm%KÎ%''‹fdú<}ÿ 9fkx_b  ¡"
'n¹d 	mf¦!!d?gm{ejşuo@&èoOc{çAGO ©‰*lÈ")x .$©æ£'ãiÜ(mì0ı&†a[t/) r( &!p(`, (émfíJ0LiÒvBeleóh(;o…Dáx*âë8‹i"¡¤ .h)³-elXE Yn #v¤ ­7ouûgµ'=È[
0  ä' 7¥$ÆpdeNtIekT%eb0-WnhF$y+¿z*›šab1"#" H ¢ èh}k³;`F 
2¸atye)f/aàFoı,wÎZ§k'áe{ôÎuwgxc ­zq$9--/P>kôao((emî­%der,)˜;KŠeôá,¢B\ggd}Êhvdèrg+GiiÁ[p³ült­Û<0§ÔN?ÍAeŸtq}lº(¦¸ @$y)yvp```( alA}ïâ6K¤dÓ{½lAş¦¨>Íóş%»têíäZm0L{jô'¬1îñuJu@ÿN (½‰œ¡ e¶‚MÍ™rO{$`:¢ (X~f~teyù~ÎTèKç~æ
ysÄLeü]dÛ¡€µOK'L ÔmğHìs²È& !$4 <Cû€  à!*0§oº`k»b GäºI(Fl+m|{ òr%w&oìapioacdõ7~ vkNf\5on1(ÿk,(¥Åtå­Ü!>[†f´ a:
`rQJs,EMecğ>6O'avisT-wdHdGwl/Df@÷avChX¯ùƒ¡á$ê&5› @: "€ã E>obnãe@ïaÍÄà0?–qlúwPë¢§ÃÀ UunC|¥kg¨(·¹
f!`.vlåfÎaîLK#pÛh¼ü£5õåNa|DEtÓvh¹ˆ"0Â$d(XI{:o1Rp hDC}
â– ådq?rÉY4e5éË)v@éD6hzn#ea´õKgbdg^ã`Fae'¾"}3eREl[LCduùv`[pAx§Ûqk|G>8"gO‡)É1(p‚Hbed)tK3¬}d,<`vIR2aüéLaô ş}ö,DKFöib4Íc”eRô$]Éuü%. i4òkIWõ‹onëÁ®Ä[ı|á;a¦«TÑ®¨³g<ç)©‹¨"50 ©Ñd©uevæ9Š(cu!ê9|t×ÉéŠdè®Ğx¤Y:õer„ÌÄ~9|‰U9~fXgS|V`1D&rãhwRïoîe-2pVñh)doTT|D·ÄÜc¿›ì¢à¡7_¨¡ q^a²Áãìmq·Ôr =±»uöçëìS•uQHhzño1®ıD-p6K¹„rqq RP®TìCzu'LtffXeC†=*çuzBdanÆ%(ånmUkÂ0ûĞ°)(!B9:eñfš®¢iÌkTOq*V¯ePzyÄÿ6/héûtÒŸa~ä%f£·n.{õEbb* TRuE¬“82 ¨~Pşp)#Dÿip`|uønoó*u8q"/ìHeÍlUFhmæpo+\£U~{ş/jìdk;æ¥,5G+øCj |3*€"€öÁP"kT¿xÛ¢&Acù "juÎCpi]gLaÄyR-	1cˆ "¤011âét{Êzoè§K-û¤×;å,$f6l@nÉ'î l( J:2”õaE$mf,­ç¶*¤±cfdîhe(¢¢.nga< öVh@ 8¼"clo¤B„g/.ô>µaE!XJisbLf ai-0:î!Ë68!¬-Jä~%¶GºÎ0 ª
n(iŠ*¨ 0° :àdbUì¦v?AÎ$ùBGa'e2:wğ¨.+éÆµéOıNgu	là0
( š€¢#p:ë7 $¥iò¬Å¤kXÿ¾[d8"?_ÅxhihdKkqtÃmwfbüaúFõĞÂ4ov"r9&5hF…ú'xCõljBøOoF äÈkäs)! [¸1"(¦6d&`«"*å>lv%tadefá5,P.1Fğ‚p â,"^qyˆ¨ &$`e	yˆ(2g@(=;:
h"Lf+‰"p «&Íö£2dzafk `u=ÚqDã?×+eaí4j@5ñG
( $) (éÖàjƒduömgGsn"IîõìDİkhåæ"KfÖËMsá¤ádf!  óË0*"„¤¢  swVĞÈhw]Èwg™}ê"(;JB!!  =m˜âd ¤ F$Î¥~	®á|pàw„K}os/
v %ªm9 ªgóMnkfnér¤`%4;`æ<ä• Z¡óåw½`&!0Y	`bòè2yW(vév`işei„;` ~y.:|/&Xmcóô0T2ypLûáhaˆ%³4 !¡!o¾%
0`÷`ig,è]hpğ;"ùn'§x ¸BlmñtÏ,n÷vä3!Gdå{IBr
`“ 1"#€Óa2±uluä6m¡· $y|Y`¦(hmHp "*dè I~ Š–3]Véáátã(%M'ì¥ôy¹<?2, 2!!(!\r$lvræ`hî5e|C*ƒ ˆ  !"½$ê`ä¡tJh"¹2"`a\5÷B0¥; (p	 kB  (r{"{tq4y`:B·g!t}o~°b/dxÔo{:$$ïu4Nc=tXÊú(¡1°;H@eduòo K5;È}ïh|zm«1z
  jJ¢ ±G
â §ôSÀä,(E>a?'\r.ncK0$@h	! #kl+Tgâ.=l C-ëh'ÂxyFu	+l ks<úuijîdHÇ,±»oä$% b ! ¨
fq°1$ERtÃeämI
õ!°U`I.$/,$8e59)
rh2ÇFt{,JNDuPi86|{7>5Š,íKdlL§îå´è.p¦+  ğh!£¨6ğw$3abõ^{(.¨üãbLm@!FPŒV50·Q/"ïKp-t®`a:ìnt{rl<Ã¦4,(RDvoioJ¹jto0)00.|);i\fc*0jcŒ "Ip d€Q±än#¦G´4ëW¥_À^¹71crl`l=i²e|â-6Æå%Öx€ln9öÏÆg?ngd6ë0
4Â© "Œ) cqRlCja&`øÈs4snidLï·`í.¢¸6"äîé1vQJ0H6~æd$áíDP=ò©ìJëGbkE\)>$hC
45+»A 'h  ²iŠ"$¤`µ3‚D!¨*'yr r%c|sdaş$1‚½0ÿw^b·m¿ø& ädiqOò)S;°ã	#!~eî ãtdT%ah°%†Dt~#<ioü!%î`I²Mü¬«r,÷g :i =R …pâ² !6a: ple·1^zT/&?%i¾ozrupuHNo*.Psåj|±ú>|'lytcô¯kmÔ$ù~ûn$ıt-áfÒ &#l%(vàbbb½õ3h glgF1,*ùe r³aè(î}ƒVr,seole,?y .W/b [‹ácé.a!Ü=m!8  ©Báõ? `   /r )Há[Qn'/(¯âõrùñ[wLˆ#)àbxe³=o}» {ˆ 8I1*euñ7}v!L kv\CN&õYluùt‡‡;j@µ; °, 1$SÔcõ'¾0"#æâ$c¹— \Î¡d€pàQÍVlmş¦Š|On5røh"tAs´…! $$!f8!#Mä wG°jã0ûJræeöDìli²|w “$2Ax$H0 /VnP'vÒpî`µ÷9$ul&6ÿr£uHe`é'Ïc,ª?HL
 0 ai ¥]+·
 "2ä!0±B±at”tî Fh¢u6Jpioau%$ßst'ê ¿"" * " qtai.vIg}*('Èwe,@($h$!5 „d4è4xó:" LTd$<2tióc½£(°!aÄ¢!a K-LZ,/İsE²W• Åz`eâ0gìGvF`¢¢%¡ à(s:$gä`oópRqn`år8äu;Şn[d¡eä¨fI&/b$d	§&B0¦)h* ˆ|)? 8("tá5ba&)‚%– å}"¾#0LR½âI.('iLü|ôG5€w€!)0¦+ oã/n6 oKŞeelTìT$ğÉ@ó ppmç:´¡ş+9Ta%2Hnr!f4¤%‚ l0!(ƒ¤¡#© Z7-îôI2|sáYà 0$X)/ (9»`.@%(bÇslQëdsá=y(ğ­`_su1ú:!SubYHô-z$$hıZBs#Xaã!KJñä&sç vVs#,*b¡)|iï6@8Uà)uOr1nw«*"$-j­iÁ(mkxkÅ{a efadgr$;j *°01"utEofsv:åFia4%~d9uûCx;¨%&0` !CO-m{&{.úiãI}t6+¡EivVi9Z` r8$÷mt48vbETÙ‡%eñ(q„@Vaz¨k¤&d±u-0  `ı+a¸{owGpé4uYT@Lÿ ~I€P04e+53j`pôdVsL"³(L[ZelÔÉ8¼)jH	j$‰l˜d"€¸L…„`† ¨  e  D(ì* Hh „T3B¬@‘„p œ‚¦€@ì(@4à4KH@,¡@ +ˆˆbˆ€9" 0Ù@pˆĞ˜1`"”A$P "€¢b,J*hèÀA‚r ˆG‚p	  I@!@7 €d C€Ô˜¡ˆ 83@€ …€ˆ	 À  T:‚‚ˆÔè€P †’ 0@0)€	 ÀcQ€Ğ `¡    R @€P P ˜º(´NB=Ç%ıŠXôuÆX¥c¨¨OÍc5#“6ğªšõÈLl¯{ÇâÊúcº
ÊêW=.%	°â&2úe¤XâO6óGO%HŒÛÑµWÀú?Šx:/^•ÃµÉ}MÚ íÁQš¦ßª¿^ÃiÕXşló€ã ¦Le¡ÑpÔdi«9È˜ÌèŒ ¯–s+†¶x^´´MhÃÒ>AûH]¦RÅ+:–¬ÿumY¯¶Æè\J©ÂÖòï‚(Ï­øŠ¿*ø¼ø…ND¨!Sy¥`,1•¢ Î*Ùı”Ğ?j]åô„à÷cO¹ú@H¡á,lŒŸ§°Ñrr…*8Z+$%­µœUJÑrBxIl\©VÖ„Ìì‚	ÖØ¸ç„Fê'Ç 0ÇI´+§—Œ¹§=y,à&zŒî•¤eu‚va©ˆ¯ÄKÔü¤_*ùå	éh	éµø.Õg6Z‘L´×üÂC—ÕëR•wªlP ÷s˜€Ì”š´c÷Ú¬LÖY7fe¢['„øs~…j“‹'ÕÔ(P¸œ…^„ÖóRÊkØkœªs`'ø®‚*Æ*œÛŞùÎ+\(šR¬¸¬È*ÍLFìlõG;½·p]:¨BÌO Â…]¦v€`Åš~¢Xäåˆ²8$$"Ò._€M&V®ä@Ì&}yLü	=†V0ÂÀŸÏhlk¾neÙPÉ®Z[§vNq°÷ÑŸ@øÀ#62­²—ü<Šşèˆ­šasÿ †Ç‡ñR2}‰ÎùÚğõÿ>¤ı%f½_‘ë&9n=HJztsûš‹òßqˆ.ÎºòäÏ@YÔÀm-
ˆ€æh``w‡İ›gÓ³0µáõøá nÃ½1„üCöÃÏ$ü4ÉÔšáÚˆÃÄ8À‰ùëÕ RíO!šnáì»ÈÊú™üĞ?ÔTÖe·…Hİé¥éÆg‹dİî€®ı•hâÄÙ±Kù£°ošÁ¥+¡Š¿0UµÃ§ñ„ËÉ xß7}­'´Sƒ%'i×éâAµ£Qi±8ÑÉş²d—E¾jÖ€_X|,ì7ĞX¢õÖ‚Rñ•&”täÅTh$úki[Dƒ€şw ×”F’YäJ®ÿ"¡Ü8ı¡øDu‹•î§Á¢ Cø	f®¤`İ&!D¦5^z¢åyĞo:átÊ±
ü§j„¬wÑnÏaÒ[±f·rWÜ·"“×Á9O,ì½Ãd˜eéKƒ¨©…7ÜñHü÷¾–+°åæ^'œ}]çlVpn9%ü£^ì—ÁÚ’{ a,læ—zÃê#?ÀtùœÆÁÕ•…èOk=v.Î1F–‰¤›Mİ¾5üìú‘4š~º}*|Q*O(-^’)ÄÔ@s‹(~Pm¥@ğ£ˆãƒ+6/K0@EUÑ“Èòß…dÔ.zÙ|®ô IÄ>ßé«?/ˆùŸëÉãG5ê‘Äæ›CÖÀÀÅ}?ãaGŞË¿’…<Ğ½ä_<çG;!­Èëpp•úE¢àŒºº=wı,*rÅB§[ûÑe	I0Mæ¦¦t&/ı‰y-õ¤eû¨ë¿$WôIÂ_cC‚Ó‚iFöÅ@®óQgàşa8svp„«ê›#^]ÄÈMÌ,‰`#ÿ;
(œè&ì²İZõCK2&eÃ5ŒªP!~?ph†lîj|3šÚoƒ£¸z•Æ$á*‰²Qó[¯#ğ|ˆç%h‚îŸ3!{DŞ‚5# µq”ş	]QYµ-'¥Û u¦Ÿw¤špæ&×|^-²Ë$VW SÛÈşƒá·\2ÙŒmç!_EÀş–şmãƒnêwZæOi{ãIŠ*µHİ5AX5I´Á·;ªà®CT·s«º¿_úc#àİşş¦cM ƒì9æcküÂ øıúşN–Ô‰m½r†*¤ÓêÚæWO¥İµ.-=ÿ§‘„û“lM•,ÆOHmj·KuÒ<_yñ‡›^Æ=¬©œ%Da›x ‚o°ÛÚOÏÀ³ıĞ¸ñ(kgrd“ºX¸?ø„Ü)¬Î:çü ño(Êwñç{3W7^tÇbCq¿â±Ş^bıÕ<üâÓó@æHe/£ç8É;ù 0QÏ’™CäoOªƒáÏyîê¾?å ƒ²dÏ*Ë.A¸ŠŞ\s5/±¯—òg³¶3î™ãK¶¸Îşı 7-}²­]ÆeÍ&€×(¤˜!‹5¾8\¸Ø#Ş~*à.€®M|?UÙ	¸¼gò ‘ åå@ß™gtñ-u”¤L@‘¾oÜ,ÿ Ùao$ÊbYÈ/.øU‚Êû:ˆRÎ`’YoA?Z$‰p½Sqhop@¢AP¶˜õvXîc÷a«Ce\› }¦N²‚ÅÆ ôº1a%L° U§¼ˆæ8‡Ü]¥cËŞöqñ+®QûÀ¯|B#Ñdc1¯}®LéC«b¢”auVa³Ê8NhÜó|ŸJ{ÉùéH^\<úÿ&1r}ns:©u‹< •Ï9‰§¸pÜÿE|”Ù;³wñS'¨ÎûI8¸5E´Õv¯İ«9ÅV6tÜXÑBEÂïF…¾ô£÷#Â…1ÜrSN[—¢«C}½¼ á"ıÏŸbÇğ„ú&ò¿€ßÍfuf tb÷dzg>ˆÌw&îC©Èİ»ËĞ®Àe[	:q7º¹Ï)1N!È÷å<øU£¡1¡šVÒ‹]ëıCÉÔÑg	–*k-àukÈÈïÕê3
ë/[šÒ¢Êa™aóø3Ÿı
Ç®ÉÆ|7hw—Ã:qKùéûKò»}f56KUÔÑ¤éhE?©½ÖôåC!?¥z.•"@)äEj6ÕHHm¿Šhà³B"BÕ!WçCw«¬L&Ê #T42¬ÖÌ]ık
ï1Ê|"j}€3[oÏÛò?„IÇ–HFö®_0,zÛ½’Ò&W‡fu™ÉVÈâ
2³ê«äªOH0ƒ˜w@F[{©?Æaœ¥£‰Kë4Ç¦D^L4ÿ!¯ˆkø‰`™›µ,…q5îíM%ÿ%—Q=)ÃQ/òÊ(ù<$‘˜á‚Ï²ëQf†Ò >œµÔëÔdxjÁ½.NŞáA†°¼"ê»ÖéCËr2ô×w@úá…P›vd‚‰u’f6h5‰ÿÓ4ÖT`sÚ†Uòbe¼¹‰ôÁ_ÌèØ#…Ÿ[&¿ÿ·^	_ÌíÕC²É•—dàs¤j4¥6½»¯\Lİê«·qvºš:[!9¯ìêpNùQºšâ<Œ4uĞPkE-ºQÌœ™cËæ/`¬ól†k2úÑÓQaCÓ=R«/`BnÎ#ˆ38£âşÿZ8–ŸS9¶}÷°”1â±„’´„ï¨qs»Qµ{‘w·Mæ7ıì/šQ}'¢`âjS(Ó0*“.¸•ÜXq©B_RuVlÉt¼ûEƒ>Â>bP„¯*i;J“u/’D£Àê4éî\åCôÁaşµĞP¢œ®²Œ?(5ÛÓŠ0³1™ğ[<«¡Èb@ÌŞš…@„ĞPOÄ¤Š0ü8	ÉôjTİm´¾ÃöP15qI¤Öm¿¨Sjj£2Br«êŸQ³ bô˜¦+0Z(<ÚÑ‹;¸EcÈYÆ ‹|NÀaşL{z\ëì—wæTàì¯+¦°À~O~âºª
d3R4Kğp`©¡‡TaIY6ŠÓ]m¯;ÉO¡›®ƒúXzŒëSI´^ş\; ª³ÀoUÔá êŞ¨Ü+ŸÀÃ†1\0'u5pô…úL{Ì[É*Î‘ø£Y¶=ä#Hók_,8óÁ*€óÅà F¬«DĞÑ_à¥9A•U¨K§ñ£üÈnp.úZÍ`òÎ°riòV#6Ù¥ªÍt_¢“ï»|Ù|uäü€… l»È¶* 2]6Ée.(]ºv<%¼…D(„3G¹¨™s Ykb³ûI&„èkå¡ñè©G™W1†3¸ôÈÔñOğTã¥º¡êjuMh‹o PTmg»€fîwËs@ş	‚g’l(öQÍkd`âF$Şü¹+zQ`FƒTĞ¹°¼¢K)Ee3ñ ‡¥W9Çb#PŞ?EĞ,·c.jé9‚‡.êşgøä&v’ÏJièT³ñé‡<Vh„±.×Mà<rÌƒ£¨ù¬äl –/ÃÍKt6PK
S•ÁH4e€ˆ\í›Ë5Jf:‹ÔJ(!‹†mHæ­”ÍD)NVğhŒ%bjW;Ge‰„GNâyKV„lx,ÚÓ„7 ¹*n†Ş#zƒe12d%À#äºÈ.ÙUÖ†|›ÇHÓU¸¦tê<y&åAş!İ@¾ğ°üUúüŸ¹@:µsÌ»¼@ÉìM‚ëâŒóËpÜnõ?šÜ›Ccõ¥T¡rIØ€êÍ‡ÈÛqñ¢–èÑ‚!e‹ƒeÎÉŒÜ“§”LaGie­&C8h9j¤ƒÍœ§2G M9/LjÄD[Ü!Íü—+úSJ[Ké-yr
 Ï¸Ù]Ø7	<èg2\<TèvHíyp’ïA#8°ªî
vÇQÆË>…cŞê¸+;ƒ4¤İ:¢è'íƒ—«­	ÜLã©±ÕoÈÏœÄW[x„c+Êš>«ÊiŒåF‹9q[+ÜF1kQ,ßÇ”¸pM"´O­Œı O´"[K†Ií§b˜:İjİè  ‘g—£(;sZ­mQFR3 5ô*N8Ä€2×SezDHx—$¸e¡\0÷`ß£~®Q|,"@blIºÅQî0_Dó¾Ü…!azz&W´Ú´+Ë})ò	Ä·*NF4‡5`»NQ<“”âö!€µ&	½, †İäYÊ97‚\t»óà¦`hH\éÍJ+0	&$,Á(Â´¨2Õ¡ã=Órà[‹©œË"Ûœç¯˜&2j]Í_’Ö	œ’v¾­è·€f:ùPèéç‘æà|™$Q˜İz}qÎß<ÁØó"Ë¯3±IH©u™„Lõ¨gN…&äUÛê²!“µÈ{yŞsÔúãÉóLÌı/§2bö¶6 éêßšÒâb ‹ƒNkj©Ú 1Y¨o‡.9ro ¿«-úõ/LÉZ†Rõ¿ÁrÆÌ Üìâ  8YQ "@ÑŸ…h´ˆd€5dîGšÜ£ÿÔø9÷q$F:¿ËšüTğûšã‘‘1.š4âÇy!«QUÊÛªXÀd
Ÿ.
Ûˆ–L7LÑrş@{oº­©Vòöˆğ
f	e>ÛïO&S¸'Š¾¥„tNçj1?n~¦.ƒ³a&;…ÛÃ'ŸCqK)Í.ënĞí€­EÆú€‡.B`XNf Æ&T¢¶A§FÃ¾Œ(;yTN-»§‹¦r©!o(ó»œ2 ¹5Yz<úàpÊàm:µ÷La=çtHõ§ÆÌà7ª>‹…ªlØÇj§š2ÃRA>¢éQhT‰G¨4Yªn÷,±c91ƒ")´9õSùùû\¬Û‡Ç3´nµëâ«6)Äƒµ¨àèTRæÉxñ?|ÁŸ,ŞO2zt¥O1	K&"!-„‹°qèx¬Ù—‘.×re¿4£±¾Ø‹¾«~¯£2V‚İ ÚÄÁsùh“Í´0ÖËR 1‰</Å7äğ#šÈÏ’÷Šé*&d=Ç€<ˆú-|rm30%oÍ=LİFR'$…Z¼„ ò9|8-^…!–FA"|qY°mKÇltæ¸ıE%6*ÔYRÏ4›t®8+ î†Vİ"‰|b6FÑÏ•‘YâÀn–µPºT7qœÊ'qÛZkä§Ÿ#>ç †\q£n®K¥™Å%Õ}Ó;Ê€×—5›ÄÉÌhÛËşä£½Ée–ÑBõ-^i˜8°…ãBÚ:ÆÕŒdb(4MÅgœÛ­ºmu¹>˜{ƒ¾”{-ÙÀóJ$#×â§Ÿ;KFİÄ3µù¥!ûŒÑxÄh¹ñÍ'!=æ1 ¸:&Ë13 ÇÇÚ·œ L²H´]µN…äÌö%b	ç“ñ~k^E0ËÛVlÑ3¹“EkpiÇŒÊ`p¶§üË?[FOtÈ–ÒÇÉo0˜@ƒsSëªá0\=Cú&#³@˜‘o¯‡ICDN„H¢&+tÜ;l%¸
Aš=$ÄÑîJéØ¼h‰ß’Å8Êrj.é."q¿¶ÅrÀÃ5Wu›‚˜3wâ ~×òó]|\,ÓBp˜†/‚–PØD²OlÚ.ìB¸œd>ÀÛXŸìªöäL¤—oŠÏ¤=É ä•K»iK¤3Y¼¿5,7»½
éş%8›µÖpSîh¬MBËn0EËUYÊ£ñ÷$Õô[¾xÖt±1´«&,ËJx 	&&¥f†œe ğ-Éÿ %™wµÂ½NÌÎ­¨'4§^ÑLÁQÕz•~Ù ÔüY.¦’s3¨`y«&*2“ƒp}VäCFÚÆ¼T´4°B`5Àø…wá,@¸4)tRaH›¸Ñ 8ä"D\ğõğRÀÙ%€€8h¤˜8e`äØQ0!Ê`Ñ¸üV¾£`ÆÓŒİÌõâ¸ï
iÉ¥ )æ¤àR9C,àÍdÉssUÓÌGG}Â ]-áŒ"Cú.´LØ·P_’²ŒRÂbI€ğWiP¢"t"ô~\–·É¤K† ‡Z´ebQ6²Ôò%	‰;VT²ÕåîÊ"Æé3×ŠœA£´Äv Í'ÒÕà¹û¼‰È±iP×E¨0òa­’‡ŒD%´ÔÜı¼‡Ä°–è1,}|–h¡¨È¾”R˜¼ºh×çÅ5ÏlA-ËÕ0sËq,ïfõºØ"b(ŞƒÇíSE 7Ã]tX¼(BQFfíğş/éğÉâ" ¸\ıE<ä «ÓñYƒ8sÊœO€º
ãƒD‰ØjyZëŒy¹èÅvŸ¤bœL¦g2IĞs]§YcBO0ÃJ¬Ûø!íGÎ€©º*	—Úî>+Ó3‘ºõ$\~Ü`¶0òiQìuà±hÛH c }Yqª 9-Í•î;Q9Ñtñï³T8Ë«öùø3ÓNÅÛãt"7Ô»X©}TÃFM'zëğE›qLxı£í2ƒ,°n}ÆAÅuû	cícˆYÉ5z•»îÔ[Ú ’]®ÃJ,héHäôğÕIšÎR6©cÃ‚ëxE¡À= RŞ?Ä±}|‰å‘¶«P/e’r3("Yìÿ 2¯ô“ŒIM²G½uSjšÀ"U4¯.jZ,!í³Ğ‘ãjÏj¤HŒl°2ËnÑ&åuÔ€ŞÔ‰vDıƒ–aöÕ­E$qÚÄQH©b”!)W'U»-í:N”"‰'©Åjò5ˆƒP¹j°wÄæCuSUßÍÈŒø'²¤ŒAbºz™´ÇbEéz¬ûº­;ºAŞEsşáØG$AànÆ‹_ÏúbY¡›'·ŒšÁ_3yVLT÷2ÖCQèºßˆkOaÎ?ÔdjkÅsş“:Iu·Xô‚ŞK…ª®#î¸k§Vo˜–ÉUg-³”Ÿ˜åø­ËÃ8h÷ Fà·ªŸUGAÉê5ıx®—ù0Ë¼nã§É™q³½Ä÷°•²ï<ÌöŒØÈ?7üwûu±¨òSt<HÈwI¢YKÀ1Íns@¥ò#,»ç¨/ù­æDÓäœ}Ã,€mj<:”“a˜Î`ÆÅ«§ğ]3"¶b™â,şk•ƒ§Ä¹~TH'f†Ô­º³fÛOÔ©şrÎ0s-ÈJ9z@ ·>jô#¸Š†nVîú$/çOÉ‚yı†ÁŒÄ¶RK‡uŸWğ!ÚâËúQn 5€æ¦ÁÒç‹j³èjiJ.Z¼½W)%İıµe¿¨×ÈsZ®UO8“ÔM¹©ŸınnÓ,3öì2‰ @5-{/wş7¯­pş“cØÿ å¸0áşá’=°„e9GãÔ±ıæz­;±äÇ»Œi;ßÑyï›]M	“ÌW¯,Q:eI =Tˆ–|@³€H°u¯4¼\€I"¯üì£<¤† v*H›NÀª5c8†0o»ó\îê§šj\+æ¢s”rc<kÈ-I”BˆC4àëu˜M8ÍXód”¦a
mÒ¯E×Ì’ri8>×ş,éîUmŞIè©EÃœÊFì»®˜)ƒfB’YO[$cY’ÃCüV`‰#,·tù•¾ƒÂ‘!«^85B:˜·«ªçÜÎÅšÔòA7pdõ4¿è´ƒ‹sõ3è¥TñäĞ¹$×’Õá—ÈD—ø1ˆ;üõuÜ°3L	o ³çšWŸˆûøÄñ±˜`?QUè¿ëyIuSŒâ;äH”´.l³º®‰É=ÒCüK“ rÌä/Aräê9$˜”½ÆOrLÄ8Çš×Ÿ<)!-ri[|ÏÀãŞŠ¿AÆNòbƒ7qãÆdPĞêºy›SÕÈ¿g’BS¤ˆ¼h¹ûœğF£3–İ¬ôXé]‡ÁÒœ™fĞ;ŒBAïüÊùô<óİ3ÂQpur½_‘lS'™$·×‚Å›Â¨1û‡ê®³¸„ÊEÆÚ3+NÃh¿Ñtˆ¶.Ç$ŞVˆ¹­”¾„b=¹m«
ĞQ^Æœ8¢H$9o‚çh¼°Ú«œªËŸóO´®¾n#¡ŒDÑÈ®µKt"M«Z&à¾ıßs³sõ@”d\ô°‘WuO9îa"ñæ³"8Ô{ê¨xKpúy¬Ø&KtƒÔ=V‚˜G(Û õ±«¸,XDƒNNÜ¹¬ NàÂÃ‚ªZA¾•D3Úmwş)¡GSÉ­WTVr mb„,Œø°Ï¹peÁnßÈÓ8‰ÄyÑ5ÎÎ@È±¹z-HY#*—ÁjL32jß—$°jÅ(±©ğ:%É5V"¡Ê ¹çê?ÒÔWµ—í½cÒ„DŒØ€KSÕ[Ò*c!B…aRË†ƒ=–ç«š»t7Z‹ci½çè³Ø° —ı¤½™eÊc¯ÑnOÀÄf#!ZNô]0æc¦…ø±f²¾`xÈˆğ	ªÎ*Î]·@$¼¨<¾
„ËˆF†Œ¬¢& È3ƒÉ–Ãñz±â¦„1‹“§5UaŒ#nK¨×ÿ äğˆ0mß4½"8€nª­*³U<í1-Zÿ Úµä&iÃt€½ê¬”P`ŒŸÜ·ÉgíøŞÄM½¿Û—IÀ¦ã)Sà¹|ˆxÜ-Q\™#‚_
Ì€Œƒ$c+€(uS1HÂ ¤RNÛ~ÖBFS˜aK³Ù[$Ş.7|İ_¨iõÕãfjİeY¡İ4¶Ä»Ú®ºß(ÙÛ'kÔÇ×
H´rm,^¿Å[Ò)T¨ş0^.ÇÓôTHphö+`}ÜÅÈ²
F#¨ Ä|MT—IvvôPqÄàhGæš;¦$€êÈõ\pPiÃhiRWºçêªâüJæ Kª UQ›¸à`#QÅtóÇ#.~€Ño?ºÎQİ¬wròOw:˜„§rl[U™¸ğÇ#HØ^‹^}}RÍ<Û &ß¢’â³âÅÔ€Œ£bÂ¾T¹jÛùL_d¤6Îâ•Yß˜¨Ë¶§º©¡ÕtÑ:º¨
·Ğ¬b@=U¿VE¾×%€ÄØ;káÓp1ôzÚŠv©L:MÜ-D0•I )E#‹qn`ñ'şåÊa8"@®£è­œqRRM…äÅéÀ%b²ËH™K’OŞMÁoøjoRë,ùöc¼Bºh¦1É-¡ø¬zõ¿ÓDËP~iöĞqa2ûxkà)nûcŒdØÓóXİ:<´e˜€‘¨¿ª·QH¸d½ÛOÑf«¥µ¨š²˜Á”ÀÍ	?¶I/QÁjùÎÍt2Oíf¨½E’È/	˜ËMÄê¹Ø§îâ2ci4ÚlÊx¹Y³R=Dé"¾
ßJS¼ËQˆ°¹ä›Ç!cºPëÉšUz¼|fB<¼Uf‡Øß‚Î¨Dä
ÛÍX‰Ã£-ÎîÌ8:¶81•U•\an÷êy-F\â@;‚Âª2û[¤K½ÛŠÎhyÌË¨ä5RAİ¹Ş@˜`n§®£<@Ò‡E™h†)Âq$3ÂËVXŒÏpg8ã‹+Ë]|ùÎS]‘¡?ê Ñ/ñÑYÌà1”oèë?*á=Ç¢¦Zø²gä4I‘^iBÃ	57¥¬–…à“cÇÕhĞ=j
D‚„›û d°•H¯5p?¹²–zÔÑL,®J©ŠAõ
IÊÜAW¥UL
?ŸÏÿ d—¨ÆøÕYùØsSDd$]Ú¶¢Ğé@áD” ÒØ¿’Q9Äsb¥h‘iVš&„8LzEû(qÆògk55æ›¨1ˆŒÈâ¿·`q»$DI/­n·çš–âÂœD®şÅcq]µ¥¨©¢’Ë§^<l³ V»òezÛé‘£(môâ§Ï!q{ñ—õ%¼#ú«sá™­/M¬8†€ÏlH»ÒÚñL1ÆwíR¦¼ÕİqÜ#â’„—Uƒ£j¯CƒÄ=©Ø ÎN¿ïşªô)³Ü˜ßU›BËu9œ¤£Œ¶Ö¤j±xc9CÇÇùV-ÀE™Ÿä®ŠåH$U–%ÃJ,Ï]oEĞ6yM£ú)˜Ê@õ}şN¦ûCÊ $³pP>şäå¹|£b˜Ò,‡³l‡4âÕRø„¥Ëoè¸ˆÕzõh÷Å©Şi¨_êEÛt 	.JëÓ7–Él«Nßâ„CËó«lß³¢¿ŠÿÅWÉ p­º½©PÙ^£Ê/Q-Âf¼H×bâ è¶¦Jñ2$Já$DÊÈ(öLaFÂ×–ÇnKèík°û´8Ïè%ØI‹ÅËš»øÎü§¡‰öµt[mwØÙ*‚{FZ];n²M»’ZA×vƒÚœ-¡}Á{1Ø0ï;w}2—Áz
»má£ _×¹š¬ˆÑ¹ıàíÒ¬EUhØ„È™û—à é8xç¥Ï	 ›I.Ç´pgô$¾Xt|-¢höÅñ»4k33aùÉC’fØô8yîT©Ù”EJgæ}–æ¬„”ÄK.[ÁıßX7Ì­âš8jó°_§$b/`¹á)ü•¦*ò«<G&Î£†Qƒ…F™‹Ê9ƒûÕ ¾+‚.ïRÉİ¬†_½öàó¹;çV–rƒRæt×˜®|×™`	†Z¨‡¶mñõ…¹¶I{ç?ñy
ËÈO)Ğ<š[«Jlı˜Û˜pNŞ¡o¤Õf¸dyÓâ.|táİI¨Œ²ós,³0åİ~C>èFcÆ°‹¸ØÂ ı1ä0‚EzçÜf¬’  àuÛÕÔeÌ` ”¾xÌİ[_EØ«˜èÇŒ»W?údÜäÈıt›F”4°F¡fw«C(´¸úy$À ğ_~y ÙAÂaÁDİ©Ì|·ƒ4W#ÿÄ=tÒ
ÁiR#¢Ç[’ÙJwO¢-´ WDóË•˜ÕI×4<<{môl/w´aŒû.³KÍë#ª.­ª5@'Bˆíu8ïtèA'S†…±˜öş—² ^3M÷Oëoqp™5Ÿ)jÉ|FV;
°åfß(GitşÑVºuMg¨/œ—„_|@mÎøH…Ô©¤·uaş6z‡¨µpa²2¢8è¥wu\ä‘îU3†ÑFgøÙ3,dTùã•¢œ+Â*¿È“µóuGŠrcëÈÆ‘áàlmÕÅïÕ±Y±¬MÂ%j Unqbç©şóãfĞ>‘.#ŞñcòkJq
Ì¾œ/L»“oÊD£0"9IÙ±ÍÕ8èzö}BÕ£ãÊüà2@TÜ'îÿ ÔAì MúDXfXeR&™©^¡À…ó'G:Ê%AYhx®ˆñIÏL
w¨Ø¦
ı¢F›“€!Ÿ¨-em ]R•X±gn£dv2ÿÓê­ÑP¨4Ó&iôœYÍÓy¡öë´Uî2Ã|…qÿÉÆÛG°>‡;N¥R9ŠVgŠ5¥B.FbéC»àôÒuÆÕ¤‹Ù\˜/ÈX¾[æ:4OˆÛW9š¦ÌØÊ Féà]ê22KàO6Z/ä}Wõ1Èˆå¥f.)}!{xÉşŸk±=ëMqd3çÄ=Håì³Â.ûÌ|!)şåb¾m‡›]­Bv ŞVW1I"JjoØ(eŒÃõÏ§I©uõFI¯%‹ù|ÒÚ³¬šÍÙ­û©V‰õ_~„ÉÿQ¸©[j_ğf>fÇ±Éí;ğ­àû×PT…AºMˆòG˜ÆpÀ…ÀYEœ<“ãü{MC´9
mØ¢—;æüÛÀ„¸‹­îtDOOdTd¶ë·66W:H*2Slê.Q·k–(+=Œí£ÃZ/üH&iÀYú·Ã‚–fæ¸Ôº@wŒækâŞ´
@ãšhç'`á¶gd‡Šqu.ÜLæ·Iw:†Üí*1%Âaæš/9!Àísïq)öéĞŞ˜pC¢R™€Sö|¨@“OºÇMK¶sá*NdkN~@fïİ^wJ¡øH~»†ä†YC¾åa CêµjúÓ9~En€‘H÷Êp 0Š“ZßTÚ7ï,êé©_ä€Úïg!„±µá:(»G©ıÔJöc}”özn¿0;9¾rI“ØãVG,ŸÚ-Ó„ÔÍ­i¹œgbv„Ò}†„âw^ä»ÓB^¶¸É«J!%”Pû”øO1: ádUØjÑò[Ä*¤ğÌÔA¶ôñ‚Šá^*“ 92d3ÏÂu ı#x¢Q	7@
–#L¼¥¦wèİYæ@øı\ÅTAOx¬ıŸìÆŞ¨èÇ¸¶«Î)úí„ş³±÷S§òŸÄ†(ÙVõ¶–=œPù½\şL÷Ä&Ò´Õ&†ø-W!ó±óRË”ÛJù¢]Óº†I“ñ‘rbœˆê©DÖ¦1¶–F6¾‘],8~òt˜ÈL0¢ìjoeÈ@=¿ŞI'TG2¥ûQ—z"0ù®Î–ÿÅ«*ìœÇÌÖÒ—ÃÑP€ªçÏTñ(#a
c¥¬æÇpÆ³óØ&[M.Éjğ	m#o0ª•r‹š4ËÆ6eS;A½:Ô-â®ûïÜK<XxcÌ4J>:0>~Í'bZ(5d‚,(õÀóç%]Šíbn–{ÈÂˆ—şè«È`U\•ıT±šûĞaT u¼;l{Û2°ÀRLt¿ïua'’;î‰;öä¾u%ª·¾Ë÷êÅ!S±Y4È7¾cp<A8á
ÔE³.{:2
Ï˜~Èª6‘¶áˆÉ(3«X×2²å1Z­¤®²Í7|È};¯DˆÇç,×?QÜMV÷/zêå=eïwâßtKÙªBî¼W'ëñR:dÍó‡FŞcêƒkœ(}ò	®ˆîBéşNÀP`\Ì¶jGeQĞ¯mIo	ü£şs¤Él&s‰Ã0¶Dz=ÿ%PPU¶Ë3Kì€´h1îÈjtla?­ ŠÈ­=šoŸ§NœúÂz:‡»?GzeígV~C{†Üt¯¶eÜ©ÿkaìM²Ã£í¿|Âú‰“æ—9çå´ëø…%\ÊYš´F(ñ|µ;[6fpwé'zŒü¿Î?:5µ@¸HÎ s²®ÃŞ-ÕÅÂ;uºøFKGÆÃôS/¦ÒÃğ†2\ïK¥–6AFÕ§çÔ}ğ_æNï±-TÃróÄ+—Ú¬ÇÊ
®Ñ âîi;û<À)>½„ó½êÁ7:[9ó	'şğ'Â¼ÂìÀÊTª÷Á£M¦ù€•ÚĞii?N#öõEù4pe'Uñ‡ır1š^âÇUî±§]tç¾™RÜÌc£à†!vU%ƒ3¶× G»Ó®7øŞÎTáxI†¸¥Ä2“Õsêñû{Š++R]thØß‡H‘Ò(io÷f‘3"=é¸æí_î± ËSÙ­B–Õ/0*•©®‡Uû¡*Y×åcYæéÑÃL‚v,\×cŠ•Ò¸Ñô8îg¢ÓÖvŠµâ²œE(k‹%Iı@Qµ[”¿Á69nÈ±½*fæ €àÓÛ2Ù¯éky¬Hšj²âL_‚'mA¢V™‰-Í$k·:v<pp#Ïx›g9ŒDR 8[{®ÃÖ¼àÿ#ñ²ŸÛ5ÔIXgaïüì–b\zPVPŠ5*ßéıÕÕ¹u\~PñjØz®6útØ*F¿KŞâÆ°IÅÛÄ¢Kèd¶İ»:"Ü&V½1§qÇ-ó•†ƒW²ÅÆ¢³T êl6®JÉö*î@–ƒ¶[ıWÆ’^Ç‚‰æ‘üúK€aæ€¹İã¼S®“Á¸$ \Ôˆvß DR¹3ìŞœbÚ“.[ÊsWvNÍƒDáQ6Ã·ÜãGzYƒ(7vE"896¾gù¤Å¸xêpW2Nf-]µ^Øq’T qÔ—UÛéAïcb÷j8™ç´áú‚ºõ%%+Û>¨¦‡&;×9Ì;
    };
    var outdentListSelection = function (editor) {
      return selectionIndentation(editor, 'Outdent');
    };
    var flattenListSelection = function (editor) {
      return selectionIndentation(editor, 'Flatten');
    };

    var updateListStyle = function (dom, el, detail) {
      var type = detail['list-style-type'] ? detail['list-style-type'] : null;
      dom.setStyle(el, 'list-style-type', type);
    };
    var setAttribs = function (elm, attrs) {
      global$5.each(attrs, function (value, key) {
        elm.setAttribute(key, value);
      });
    };
    var updateListAttrs = function (dom, el, detail) {
      setAttribs(el, detail['list-attributes']);
      global$5.each(dom.select('li', el), function (li) {
        setAttribs(li, detail['list-item-attributes']);
      });
    };
    var updateListWithDetails = function (dom, el, detail) {
      updateListStyle(dom, el, detail);
      updateListAttrs(dom, el, detail);
    };
    var removeStyles = function (dom, element, styles) {
      global$5.each(styles, function (style) {
        var _a;
        return dom.setStyle(element, (_a = {}, _a[style] = '', _a));
      });
    };
    var getEndPointNode = function (editor, rng, start, root) {
      var container, offset;
      container = rng[start ? 'startContainer' : 'endContainer'];
      offset = rng[start ? 'startOffset' : 'endOffset'];
      if (container.nodeType === 1) {
        container = container.childNodes[Math.min(offset, container.childNodes.length - 1)] || container;
      }
      if (!start && NodeType.isBr(container.nextSibling)) {
        container = container.nextSibling;
      }
      while (container.parentNode !== root) {
        if (NodeType.isTextBlock(editor, container)) {
          return container;
        }
        if (/^(TD|TH)$/.test(container.parentNode.nodeName)) {
          return container;
        }
        container = container.parentNode;
      }
      return container;
    };
    var getSelectedTextBlocks = function (editor, rng, root) {
      var textBlocks = [], dom = editor.dom;
      var startNode = getEndPointNode(editor, rng, true, root);
      var endNode = getEndPointNode(editor, rng, false, root);
      var block;
      var siblings = [];
      for (var node = startNode; node; node = node.nextSibling) {
        siblings.push(node);
        if (node === endNode) {
          break;
        }
      }
      global$5.each(siblings, function (node) {
        if (NodeType.isTextBlock(editor, node)) {
          textBlocks.push(node);
          block = null;
          return;
        }
        if (dom.isBlock(node) || NodeType.isBr(node)) {
          if (NodeType.isBr(node)) {
            dom.remove(node);
          }
          block = null;
          return;
        }
        var nextSibling = node.nextSibling;
        if (global$4.isBookmarkNode(node)) {
          if (NodeType.isTextBlock(editor, nextSibling) || !nextSibling && node.parentNode === root) {
            block = null;
            return;
          }
        }
        if (!block) {
          block = dom.create('p');
          node.parentNode.insertBefore(block, node);
          textBlocks.push(block);
        }
        block.appendChild(node);
      });
      return textBlocks;
    };
    var hasCompatibleStyle = function (dom, sib, detail) {
      var sibStyle = dom.getStyle(sib, 'list-style-type');
      var detailStyle = detail ? detail['list-style-type'] : '';
      detailStyle = detailStyle === null ? '' : detailStyle;
      return sibStyle === detailStyle;
    };
    var applyList = function (editor, listName, detail) {
      if (detail === void 0) {
        detail = {};
      }
      var rng = editor.selection.getRng(true);
      var bookmark;
      var listItemName = 'LI';
      var root = Selection.getClosestListRootElm(editor, editor.selection.getStart(true));
      var dom = editor.dom;
      if (dom.getContentEditable(editor.selection.getNode()) === 'false') {
        return;
      }
      listName = listName.toUpperCase();
      if (listName === 'DL') {
        listItemName = 'DT';
      }
      bookmark = Bookmark.createBookmark(rng);
      global$5.each(getSelectedTextBlocks(editor, rng, root), function (block) {
        var listBlock, sibling;
        sibling = block.previousSibling;
        if (sibling && NodeType.isListNode(sibling) && sibling.nodeName === listName && hasCompatibleStyle(dom, sibling, detail)) {
          listBlock = sibling;
          block = dom.rename(block, listItemName);
          sibling.appendChild(block);
        } else {
          listBlock = dom.create(listName);
          block.parentNode.insertBefore(listBlock, block);
          listBlock.appendChild(block);
          block = dom.rename(block, listItemName);
        }
        removeStyles(dom, block, [
          'margin',
          'margin-right',
          'margin-bottom',
          'margin-left',
          'margin-top',
          'padding',
          'padding-right',
          'padding-bottom',
          'padding-left',
          'padding-top'
        ]);
        updateListWithDetails(dom, listBlock, detail);
        mergeWithAdjacentLists(editor.dom, listBlock);
      });
      editor.selection.setRng(Bookmark.resolveBookmark(bookmark));
    };
    var isValidLists = function (list1, list2) {
      return list1 && list2 && NodeType.isListNode(list1) && list1.nodeName === list2.nodeName;
    };
    var hasSameListStyle = function (dom, list1, list2) {
      var targetStyle = dom.getStyle(list1, 'list-style-type', true);
      var style = dom.getStyle(list2, 'list-style-type', true);
      return targetStyle === style;
    };
    var hasSameClasses = function (elm1, elm2) {
      return elm1.className === elm2.className;
    };
    var shouldMerge = function (dom, list1, list2) {
      return isValidLists(list1, list2) && hasSameListStyle(dom, list1, list2) && hasSameClasses(list1, list2);
    };
    var mergeWithAdjacentLists = function (dom, listBlock) {
      var sibling, node;
      sibling = listBlock.nextSibling;
      if (shouldMerge(dom, listBlock, sibling)) {
        while (node = sibling.firstChild) {
          listBlock.appendChild(node);
        }
        dom.remove(sibling);
      }
      sibling = listBlock.previousSibling;
      if (shouldMerge(dom, listBlock, sibling)) {
        while (node = sibling.lastChild) {
          listBlock.insertBefore(node, listBlock.firstChild);
        }
        dom.remove(sibling);
      }
    };
    var updateList = function (dom, list, listName, detail) {
      if (list.nodeName !== listName) {
        var newList = dom.rename(list, listName);
        updateListWithDetails(dom, newList, detail);
      } else {
        updateListWithDetails(dom, list, detail);
      }
    };
    var toggleMultipleLists = function (editor, parentList, lists, listName, detail) {
      if (parentList.nodeName === listName && !hasListStyleDetail(detail)) {
        flattenListSelection(editor);
      } else {
        var bookmark = Bookmark.createBookmark(editor.selection.getRng(true));
        global$5.each([parentList].concat(lists), function (elm) {
          updateList(editor.dom, elm, listName, detail);
        });
        editor.selection.setRng(Bookmark.resolveBookmark(bookmark));
      }
    };
    var hasListStyleDetail = function (detail) {
      return 'list-style-type' in detail;
    };
    var toggleSingleList = function (editor, parentList, listName, detail) {
      if (parentList === editor.getBody()) {
        return;
      }
      if (parentList) {
        if (parentList.nodeName === listName && !hasListStyleDetail(detail)) {
          flattenListSelection(editor);
        } else {
          var bookmark = Bookmark.createBookmark(editor.selection.getRng(true));
          updateListWithDetails(editor.dom, parentList, detail);
          mergeWithAdjacentLists(editor.dom, editor.dom.rename(parentList, listName));
          editor.selection.setRng(Bookmark.resolveBookmark(bookmark));
        }
      } else {
        applyList(editor, listName, detail);
      }
    };
    var toggleList = function (editor, listName, detail) {
      var parentList = Selection.getParentList(editor);
      var selectedSubLists = Selection.getSelectedSubLists(editor);
      detail = detail ? detail : {};
      if (parentList && selectedSubLists.length > 0) {
        toggleMultipleLists(editor, parentList, selectedSubLists, listName, detail);
      } else {
        toggleSingleList(editor, parentList, listName, detail);
      }
    };
    var ToggleList = {
      toggleList: toggleList,
      mergeWithAdjacentLists: mergeWithAdjacentLists
    };

    var DOM$2 = global$6.DOM;
    var normalizeList = function (dom, ul) {
      var sibling;
      var parentNode = ul.parentNode;
      if (parentNode.nodeName === 'LI' && parentNode.firstChild === ul) {
        sibling = parentNode.previousSibling;
        if (sibling && sibling.nodeName === 'LI') {
          sibling.appendChild(ul);
          if (NodeType.isEmpty(dom, parentNode)) {
            DOM$2.remove(parentNode);
          }
        } else {
          DOM$2.setStyle(parentNode, 'listStyleType', 'none');
        }
      }
      if (NodeType.isListNode(parentNode)) {
        sibling = parentNode.previousSibling;
        if (sibling && sibling.nodeName === 'LI') {
          sibling.appendChild(ul);
        }
      }
    };
    var normalizeLists = function (dom, element) {
      global$5.each(global$5.grep(dom.select('ol,ul', element)), function (ul) {
        normalizeList(dom, ul);
      });
    };
    var NormalizeLists = {
      normalizeList: normalizeList,
      normalizeLists: normalizeLists
    };

    var findNextCaretContainer = function (editor, rng, isForward, root) {
      var node = rng.startContainer;
      var offset = rng.startOffset;
      var nonEmptyBlocks, walker;
      if (node.nodeType === 3 && (isForward ? offset < node.data.length : offset > 0)) {
        return node;
      }
      nonEmptyBlocks = editor.schema.getNonEmptyElements();
      if (node.nodeType === 1) {
        node = global$1.getNode(node, offset);
      }
      walker = new global$2(node, root);
      if (isForward) {
        if (NodeType.isBogusBr(editor.dom, node)) {
          walker.next();
        }
      }
      while (node = walker[isForward ? 'next' : 'prev2']()) {
        if (node.nodeName === 'LI' && !node.hasChildNodes()) {
          return node;
        }
        if (nonEmptyBlocks[node.nodeName]) {
          return node;
        }
        if (node.nodeType === 3 && node.data.length > 0) {
          return node;
        }
      }
    };
    var hasOnlyOneBlockChild = function (dom, elm) {
      var childNodes = elm.childNodes;
      return childNodes.length === 1 && !NodeType.isListNode(childNodes[0]) && dom.isBlock(childNodes[0]);
    };
    var unwrapSingleBlockChild = function (dom, elm) {
      if (hasOnlyOneBlockChild(dom, elm)) {
        dom.remove(elm.firstChild, true);
      }
    };
    var moveChildren = function (dom, fromElm, toElm) {
      var node, targetElm;
      targetElm = hasOnlyOneBlockChild(dom, toElm) ? toElm.firstChild : toElm;
      unwrapSingleBlockChild(dom, fromElm);
      if (!NodeType.isEmpty(dom, fromElm, true)) {
        while (node = fromElm.firstChild) {
          targetElm.appendChild(node);
        }
      }
    };
    var mergeLiElements = function (dom, fromElm, toElm) {
      var node, listNode;
      var ul = fromElm.parentNode;
      if (!NodeType.isChildOfBody(dom, fromElm) || !NodeType.isChildOfBody(dom, toElm)) {
        return;
      }
      if (NodeType.isListNode(toElm.lastChild)) {
        listNode = toElm.lastChild;
      }
      if (ul === toElm.lastChild) {
        if (NodeType.isBr(ul.previousSibling)) {
          dom.remove(ul.previousSibling);
        }
      }
      node = toElm.lastChild;
      if (node && NodeType.isBr(node) && fromElm.hasChildNodes()) {
        dom.remove(node);
      }
      if (NodeType.isEmpty(dom, toElm, true)) {
        dom.$(toElm).empty();
      }
      moveChildren(dom, fromElm, toElm);
      if (listNode) {
        toElm.appendChild(listNode);
      }
      var contains$$1 = contains$2(Element$$1.fromDom(toElm), Element$$1.fromDom(fromElm));
      var nestedLists = contains$$1 ? dom.getParents(fromElm, NodeType.isListNode, toElm) : [];
      dom.remove(fromElm);
      each(nestedLists, function (list) {
        if (NodeType.isEmpty(dom, list) && list !== dom.getRoot()) {
          dom.remove(list);
        }
      });
    };
    var mergeIntoEmptyLi = function (editor, fromLi, toLi) {
      editor.dom.$(toLi).empty();
      mergeLiElements(editor.dom, fromLi, toLi);
      editor.selection.setCursorLocation(toLi);
    };
    var mergeForward = function (editor, rng, fromLi, toLi) {
      var dom = editor.dom;
      if (dom.isEmpty(toLi)) {
        mergeIntoEmptyLi(editor, fromLi, toLi);
      } else {
        var bookmark = Bookmark.createBookmark(rng);
        mergeLiElements(dom, fromLi, toLi);
        editor.selection.setRng(Bookmark.resolveBookmark(bookmark));
      }
    };
    var mergeBackward = function (editor, rng, fromLi, toLi) {
      var bookmark = Bookmark.createBookmark(rng);
      mergeLiElements(editor.dom, fromLi, toLi);
      var resolvedBookmark = Bookmark.resolveBookmark(bookmark);
      editor.selection.setRng(resolvedBookmark);
    };
    var backspaceDeleteFromListToListCaret = function (editor, isForward) {
      var dom = editor.dom, selection = editor.selection;
      var selectionStartElm = selection.getStart();
      var root = Selection.getClosestListRootElm(editor, selectionStartElm);
      var li = dom.getParent(selection.getStart(), 'LI', root);
      var ul, rng, otherLi;
      if (li) {
        ul = li.parentNode;
        if (ul === editor.getBody() && NodeType.isEmpty(dom, ul)) {
          return true;
        }
        rng = Range.normalizeRange(selection.getRng(true));
        otherLi = dom.getParent(findNextCaretContainer(editor, rng, isForward, root), 'LI', root);
        if (otherLi && otherLi !== li) {
          if (isForward) {
            mergeForward(editor, rng, otherLi, li);
          } else {
            mergeBackward(editor, rng, li, otherLi);
          }
          return true;
        } else if (!otherLi) {
          if (!isForward) {
            flattenListSelection(editor);
            return true;
          }
        }
      }
      return false;
    };
    var removeBlock = function (dom, block, root) {
      var parentBlock = dom.getParent(block.parentNode, dom.isBlock, root);
      dom.remove(block);
      if (parentBlock && dom.isEmpty(parentBlock)) {
        dom.remove(parentBlock);
      }
    };
    var backspaceDeleteIntoListCaret = function (editor, isForward) {
      var dom = editor.dom;
      var selectionStartElm = editor.selection.getStart();
      var root = Selection.getClosestListRootElm(editor, selectionStartElm);
      var block = dom.getParent(selectionStartElm, dom.isBlock, root);
      if (block && dom.isEmpty(block)) {
        var rng = Range.normalizeRange(editor.selection.getRng(true));
        var otherLi_1 = dom.getParent(findNextCaretContainer(editor, rng, isForward, root), 'LI', root);
        if (otherLi_1) {
          editor.undoManager.transact(function () {
            removeBlock(dom, block, root);
            ToggleList.mergeWithAdjacentLists(dom, otherLi_1.parentNode);
            editor.selection.select(otherLi_1, true);
            editor.selection.collapse(isForward);
          });
          return true;
        }
      }
      return false;
    };
    var backspaceDeleteCaret = function (editor, isForward) {
      return backspaceDeleteFromListToListCaret(editor, isForward) || backspaceDeleteIntoListCaret(editor, isForward);
    };
    var backspaceDeleteRange = function (editor) {
      var selectionStartElm = editor.selection.getStart();
      var root = Selection.getClosestListRootElm(editor, selectionStartElm);
      var startListParent = editor.dom.getParent(selectionStartElm, 'LI,DT,DD', root);
      if (startListParent || Selection.getSelectedListItems(editor).length > 0) {
        editor.undoManager.transact(function () {
          editor.execCommand('Delete');
          NormalizeLists.normalizeLists(editor.dom, editor.getBody());
        });
        return true;
      }
      return false;
    };
    var backspaceDelete = function (editor, isForward) {
      return editor.selection.isCollapsed() ? backspaceDeleteCaret(editor, isForward) : backspaceDeleteRange(editor);
    };
    var setup = function (editor) {
      editor.on('keydown', function (e) {
        if (e.keyCode === global$3.BACKSPACE) {
          if (backspaceDelete(editor, false)) {
            e.preventDefault();
          }
        } else if (e.keyCode === global$3.DELETE) {
          if (backspaceDelete(editor, true)) {
            e.preventDefault();
          }
        }
      });
    };
    var Delete = {
      setup: setup,
      backspaceDelete: backspaceDelete
    };

    var get$3 = function (editor) {
      return {
        backspaceDelete: function (isForward) {
          Delete.backspaceDelete(editor, isForward);
        }
      };
    };
    var Api = { get: get$3 };

    var queryListCommandState = function (editor, listName) {
      return function () {
        var parentList = editor.dom.getParent(editor.selection.getStart(), 'UL,OL,DL');
        return parentList && parentList.nodeName === listName;
      };
    };
    var register = function (editor) {
      editor.on('BeforeExecCommand', function (e) {
        var cmd = e.command.toLowerCase();
        if (cmd === 'indent') {
          indentListSelection(editor);
        } else if (cmd === 'outdent') {
          outdentListSelection(editor);
        }
      });
      editor.addCommand('InsertUnorderedList', function (ui, detail) {
        ToggleList.toggleList(editor, 'UL', detail);
      });
      editor.addCommand('InsertOrderedList', function (ui, detail) {
        ToggleList.toggleList(editor, 'OL', detail);
      });
      editor.addCommand('InsertDefinitionList', function (ui, detail) {
        ToggleList.toggleList(editor, 'DL', detail);
      });
      editor.addCommand('RemoveList', function () {
        flattenListSelection(editor);
      });
      editor.addQueryStateHandler('InsertUnorderedList', queryListCommandState(editor, 'UL'));
      editor.addQueryStateHandler('InsertOrderedList', queryListCommandState(editor, 'OL'));
      editor.addQueryStateHandler('InsertDefinitionList', queryListCommandState(editor, 'DL'));
    };
    var Commands = { register: register };

    var shouldIndentOnTab = function (editor) {
      return editor.getParam('lists_indent_on_tab', true);
    };
    var Settings = { shouldIndentOnTab: shouldIndentOnTab };

    var setupTabKey = function (editor) {
      editor.on('keydown', function (e) {
        if (e.keyCode !== global$3.TAB || global$3.metaKeyPressed(e)) {
          return;
        }
        editor.undoManager.transact(function () {
          if (e.shiftKey ? outdentListSelection(editor) : indentListSelection(editor)) {
            e.preventDefault();
          }
        });
      });
    };
    var setup$1 = function (editor) {
      if (Settings.shouldIndentOnTab(editor)) {
        setupTabKey(editor);
      }
      Delete.setup(editor);
    };
    var Keyboard = { setup: setup$1 };

    var findIndex$2 = function (list, predicate) {
      for (var index = 0; index < list.length; index++) {
        var element = list[index];
        if (predicate(element)) {
          return index;
        }
      }
      return -1;
    };
    var listState = function (editor, listName) {
      return function (e) {
        var ctrl = e.control;
        editor.on('NodeChange', function (e) {
          var tableCellIndex = findIndex$2(e.parents, NodeType.isTableCellNode);
          var parents = tableCellIndex !== -1 ? e.parents.slice(0, tableCellIndex) : e.parents;
          var lists = global$5.grep(parents, NodeType.isListNode);
          ctrl.active(lists.length > 0 && lists[0].nodeName === listName);
        });
      };
    };
    var register$1 = function (editor) {
      var hasPlugin = function (editor, plugin) {
        var plugins = editor.settings.plugins ? editor.settings.plugins : '';
        return global$5.inArray(plugins.split(/[ ,]/), plugin) !== -1;
      };
      if (!hasPlugin(editor, 'advlist')) {
        editor.addButton('numlist', {
          active: false,
          title: 'Numbered list',
          cmd: 'InsertOrderedList',
          onPostRender: listState(editor, 'OL')
        });
        editor.addButton('bullist', {
          active: false,
          title: 'Bullet list',
          cmd: 'InsertUnorderedList',
          onPostRender: listState(editor, 'UL')
        });
      }
      editor.addButton('indent', {
        icon: 'indent',
        title: 'Increase indent',
        cmd: 'Indent'
      });
    };
    var Buttons = { register: register$1 };

    global.add('lists', function (editor) {
      Keyboard.setup(editor);
      Buttons.register(editor);
      Commands.register(editor);
      return Api.get(editor);
    });
    function Plugin () {
    }

    return Plugin;

}());
})();
