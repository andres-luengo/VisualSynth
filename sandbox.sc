Sandbox {
	var <uview; // UserView
	var nodes; // List[Node]

	var cameraMatrix; // matrix

	var clicked; // bool
	var clickCoords; // Array[int], length 2
	var dragCoords; // Array[int], length 2
	var dragging; // bool
	var zoom; // float

	const max_zoom = 3;
	const min_zoom = 0.33;

	*new {|parent, bounds|
		^super.new.prSandboxInit(parent, bounds);
	}

	prSandboxInit {|parent, bounds|
		uview = UserView.new(parent, bounds);
		uview.drawFunc = { this.drawNodes };
		uview.background = Color.white;

		uview.mouseDownAction = {|... args| this.prMouseDown(*args) };
		uview.mouseMoveAction = {|... args| this.prMouseMove(*args) };
		uview.mouseWheelAction = {|... args| this.prMouseWheel(*args) };

		// zoomX, shearY, shearX, zoomY, translateX, translateY
		// translates are position of origin
		cameraMatrix = [1, 0, 0, 1, 0, 0];

		clicked = false;
		clickCoords = [-1, -1];
		zoom = 1;

		nodes = List.new;
	}

	prMouseDown {|view, x, y, modifiers, buttonNumber, clickCount|
		clicked = true;
		clickCoords = [x, y];
		dragCoords = [x, y];
	}

	prMouseMove {|view, x, y, modifiers|
		if ( modifiers.isAlt, { this.prDragCamera(x, y); });
	}

	prMouseUp {|view, x, y, modifiers, buttonNumber|
		clicked = false;
	}

	prDragCamera {|x, y|
		var displacement = ([x, y] - dragCoords);

		cameraMatrix[4] = cameraMatrix[4] + displacement[0];
		cameraMatrix[5] = cameraMatrix[5] + displacement[1];
		this.uview.refresh;

		dragCoords = [x, y];
	}

	prMouseWheel {|view, x, y, modifiers, xDelta, yDelta|
		if (modifiers.isAlt, { this.prZoomCamera(x, y, xDelta) });
	}

	prZoomCamera { |x, y, xDelta|
		// affine transformation wizardry
		var zoomFactor = 1 + (xDelta / 250);
		var oldZoom = zoom;
		var newZoom = (oldZoom * zoomFactor).clip(min_zoom, max_zoom);

		var b = this.uview.bounds;

		var worldCX = (x - cameraMatrix[4]) / oldZoom;
		var worldCY = (y - cameraMatrix[5]) / oldZoom;

		zoom = newZoom;
		cameraMatrix[0] = newZoom;
		cameraMatrix[3] = newZoom;

		cameraMatrix[4] = x - (worldCX * newZoom);
		cameraMatrix[5] = y - (worldCY * newZoom);

		this.uview.refresh;
	}

	drawNodes {
		Pen.matrix = cameraMatrix;
		nodes.do({|node|
			Pen.use({node.draw});
		})
	}

	addNode {|node| nodes.add(node) }
}