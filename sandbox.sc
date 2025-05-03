Sandbox {
	var <uview; // UserView
	var nodes; // List[Node]
	var hoveredNode; // SynthNode | nil
	var selectedNode; // SynthNode | nil

	var cameraMatrix; // matrix

	var clicked; // bool
	var clickCoords; // Array[int], length 2
	var dragCoords; // Array[int], length 2
	var panning; // bool
	var zoom; // float

	var nodeDragOffset; // Array[int], length 2;

	const max_zoom = 3;
	const min_zoom = 0.33;

	*new {|parent, bounds|
		^super.new.prSandboxInit(parent, bounds);
	}

	prToWorldCoord {| coords |
		var displacementView = coords - cameraMatrix[[4, 5]];
		^displacementView / zoom;
	}

	prSandboxInit {|parent, bounds|
		uview = UserView.new(parent, bounds);
		uview.drawFunc = { this.drawNodes };
		uview.background = Color.white;

		uview.mouseDownAction = {|... args| this.prMouseDown(*args) };
		uview.mouseMoveAction = {|... args| this.prMouseMove(*args) };
		uview.mouseWheelAction = {|... args| this.prMouseWheel(*args) };
		uview.mouseOverAction = {|... args| this.prMouseOver(*args) };

		// zoomX, shearY, shearX, zoomY, translateX, translateY
		// translates are position of origin
		cameraMatrix = [1, 0, 0, 1, 0, 0];

		clicked = false;
		panning = false;
		clickCoords = [-1, -1];
		zoom = 1;

		nodes = List.new;
	}

	prMouseDown {|view, x, y, modifiers, buttonNumber, clickCount|
		clicked = true;
		panning = modifiers.isAlt;
		clickCoords = [x, y];
		dragCoords = [x, y];

		this.prUpdateNodeSelection(x, y);
	}

	prUpdateNodeSelection {|x, y|
		var oldSelection = selectedNode;
		selectedNode = hoveredNode;
		if (oldSelection.isNil.not, {
			oldSelection.selected = false;
		});
		if (selectedNode.isNil.not, {
			selectedNode.selected = true;

			// put node at the top of the stack
			nodes.remove(selectedNode);
			nodes.add(selectedNode);

			// set drag offset (so you don't only drag from top left corner)
			nodeDragOffset = [x, y] - [selectedNode.x, selectedNode.y];
		});
		if (oldSelection != selectedNode, {
			uview.refresh;
		});
	}

	prDragNode { |x, y|
		var worldCoords = this.prToWorldCoord([x, y]);
		selectedNode.x = worldCoords[0] - nodeDragOffset[0];
		selectedNode.y = worldCoords[1] - nodeDragOffset[1];
		uview.refresh;
	}

	prMouseMove {|view, x, y, modifiers|
		if ( panning, { this.prDragCamera(x, y); });
		if ( selectedNode.isNil.not, { this.prDragNode(x, y); } );
	}

	prMouseUp {|view, x, y, modifiers, buttonNumber|
		clicked = false;
		panning = false;
	}

	prDragCamera {|x, y|
		var displacement = ([x, y] - dragCoords);

		cameraMatrix[4] = cameraMatrix[4] + displacement[0];
		cameraMatrix[5] = cameraMatrix[5] + displacement[1];
		cameraMatrix[[0, 3, 4, 5]].postln;
		this.uview.refresh;

		dragCoords = [x, y];
	}

	prMouseWheel {|view, x, y, modifiers, xDelta, yDelta|
		if (modifiers.isAlt, { ^this.prZoomCamera(x, y, xDelta) });

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

		uview.refresh;
	}

	prNodesUnderMouse { |x, y|
		var worldCoords;
		worldCoords = this.prToWorldCoord([x, y]);
		^nodes.select({|node, i|
			node.contains(*worldCoords);
		});
	}

	prMouseOver {|view, x, y|
		var hoveredNodes = this.prNodesUnderMouse(x, y);
		var oldHoveredNode = hoveredNode;
		if (hoveredNode.isNil.not, {
			hoveredNode.hovered = false;
		});
		hoveredNode = hoveredNodes.last;
		if (hoveredNode.isNil.not, {
			hoveredNode.hovered = true;
		});
		if (hoveredNode != oldHoveredNode, {
			uview.refresh;
		});
	}

	drawNodes {
		Pen.matrix = cameraMatrix;
		nodes.do({|node|
			Pen.use({node.draw});
		})
	}

	addNode {|node| nodes.add(node) }
}