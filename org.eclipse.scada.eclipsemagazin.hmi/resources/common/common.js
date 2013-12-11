// more common

function isString(obj) {
	if (obj === undefined) {
		return false;
	}
	if (obj === null) {
		return false;
	}
	if (typeof obj === "string") {
		return true;
	}
	if (obj instanceof java.lang.String) {
		return true;
	}
	return false;
}

function isMap(obj) {
	if (obj === undefined) {
		return false;
	}
	if (obj === null) {
		return false;
	}
	if (typeof obj === "object") {
		return true;
	}
	if (obj instanceof java.lang.Map) {
		return true;
	}
	return false;
}

function toJavaMap(obj) {
	var result = new java.util.HashMap();
	for ( var key in obj) {
		result.put(key, obj[key]);
	}
	return result;
}

function dump(obj) {
	if (isString(obj)) {
		return "" + obj;
	} else if (isMap(obj)) {
		return "" + toJavaMap(obj);
	} else {
		return "" + obj;
	}
}
