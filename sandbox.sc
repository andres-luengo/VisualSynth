Sandbox {
	var <uview; // UserView
	var nodes; // List[Node]
	var wires; // List[Wire]
	var hoveredNode; // VSNode | nil
	var selectedNode; // VSNode| nil
	var selectedNodeType; // Class
	var hoveredPort; // WirePort | nil
	var wireIn; // WirePort | nil
	var mouseX, mouseY; // WORLD COORDS

	var cameraMatrix; // matrix

	var selectedTool; // str

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
		uview.drawFunc = { this.draw };
		uview.background = Color.white;

		uview.mouseDownAction = {|... args| this.prMouseDown(*args) };
		uview.mouseMoveAction = {|... args| this.prMouseMove(*args) };
		uview.mouseWheelAction = {|... args| this.prMouseWheel(*args) };
		uview.mouseOverAction = {|... args| this.prMouseOver(*args) };
		uview.mouseUpAction = {|... args| this.prMouseUp(*args) };

		// zoomX, shearY, shearX, zoomY, translateX, translateY
		// translates are position of origin
		cameraMatrix = [1, 0, 0, 1, 0, 0];

		clicked = false;
		panning = false;
		clickCoords = [-1, -1];
		zoom = 1;

		nodes = List.new;
		wires = List.new;

		mouseX = 0;
		mouseY = 0;
	}

	prDeleteMouseDown {
		if (selectedNode.isNil.not) {
			selectedNode.delete;
			nodes.remove(selectedNode);

			wires = wires.reject({|wire|
				(wire.in.node == selectedNode) ||
				(wire.out.node == selectedNode)
			});
		};
		if (hoveredPort.isNil.not) {
			wires = wires.difference(hoveredPort.wires);
			hoveredPort.wires.collectCopy({|wire|
				wire.delete;
			});
			hoveredPort.wires = List.new;
		};
	}

	prMouseDown {|view, x, y, modifiers, buttonNumber, clickCount|
		clicked = true;
		panning = modifiers.isAlt;
		clickCoords = [x, y];
		dragCoords = [x, y];

		if (panning) { ^nil; }; // escape out if panning

		this.prUpdateNodeSelection(x, y);

		// double click to open properties
		if ((selectedTool == \Edit) && (clickCount == 2)) {
			selectedNode.openProperties;
		};
		if (selectedTool == \Node) {
			this.prNodeDown(x, y);
		};
		if (selectedTool == \Delete) {
			this.prDeleteMouseDown;
			uview.refresh;
		};
		if (selectedTool == \Wire) {
			this.prWireDown(x, y);
			uview.refresh;
		};
	}

	prNodeDown {|x, y|
		var worldCoords = this.prToWorldCoord([x, y]);
		var node = selectedNodeType.new(*worldCoords);
		this.addNode(node);
		uview.refresh;
	}

	prWireDown {|x, y|
		if ((hoveredPort.isNil.not), {
			if (hoveredPort.dir == \right) {
				wireIn = hoveredPort;
				this.prUpdateMouseCoords(x, y);
		}});
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
			nodeDragOffset = this.prToWorldCoord([x, y]) - [selectedNode.x, selectedNode.y];
		});
		if (oldSelection != selectedNode, {
			uview.refresh;
		});
	}

	prDragNode { |x, y|
		var worldCoords;
		worldCoords = this.prToWorldCoord([x, y]);
		selectedNode.x = worldCoords[0] - nodeDragOffset[0];
		selectedNode.y = worldCoords[1] - nodeDragOffset[1];
		uview.refresh;
	}

	prMouseMove {|view, x, y, modifiers|
		if ( panning, { this.prDragCamera(x, y); });
		if ((selectedNode.isNil.not) && (selectedTool == \Edit), {
				this.prDragNode(x, y);
		});


		if (selectedTool == \Wire, { this.prUpdatePortHover(x, y) });
		if (wireIn.isNil.not, {
			this.prUpdateMouseCoords(x, y);
			uview.refresh;
		});
	}

	prWireExists {|in, out|
		^wires.any({|wire|
			(wire.in === in) &&
			(wire.out === out)
		});
	}

	prWireMouseUp {
		if (hoveredPort.isNil.not) {
			if ((this.prWireExists(wireIn, hoveredPort).not) &&
				(hoveredPort.dir == \left)) {

				wires.add(Wire(wireIn, hoveredPort));
			}
		};
		wireIn = nil;
		uview.refresh;
	}

	prMouseUp {|view, x, y, modifiers, buttonNumber|
		clicked = false;
		panning = false;

		if (selectedTool == \Wire, { this.prWireMouseUp })
	}

	prDragCamera {|x, y|
		var displacement = ([x, y] - dragCoords);

		cameraMatrix[4] = cameraMatrix[4] + displacement[0];
		cameraMatrix[5] = cameraMatrix[5] + displacement[1];
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

	prUpdateNodeHover { |x, y|
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

	prPortsUnderMouse { |x, y|
		var worldCoords, ports;

		worldCoords = this.prToWorldCoord([x, y]);
		ports = List();
		// collect inputs
		nodes.do({|node, i|
			var port = node.portAt(*worldCoords);
			if (port.isNil.not, {
				ports.add(port);
			})
		});
		^ports.asArray;
	}

	prUpdatePortHover { |x, y|
		var hoveredPorts = this.prPortsUnderMouse(x, y);
		var oldHoveredPort = hoveredPort;
		if (hoveredPort.isNil.not, {
			hoveredPort.hovered = false;
		});
		hoveredPort = hoveredPorts.last;
		if (hoveredPort.isNil.not, {
			hoveredPort.hovered = true;
			hoveredPort.wires.postln;
		});
		if (hoveredPort != oldHoveredPort, {
			uview.refresh;
		});
	}

	prUpdateMouseCoords {|x, y|
		var coords = this.prToWorldCoord([x, y]);
		mouseX = coords[0];
		mouseY = coords[1];
	}

	prMouseOver {|view, x, y|
		if ((selectedTool == \Edit) ||
			(selectedTool == \Delete), {
				this.prUpdateNodeHover(x, y)
		});

		if ((selectedTool == \Wire) ||
			(selectedTool == \Delete), {
			this.prUpdatePortHover(x, y)
		});
	}

	draw {
		Pen.matrix = cameraMatrix;
		this.drawNodes;
		this.drawWires;
		this.drawTempWire;
	}

	drawNodes {
		nodes.do({|node|
			Pen.use({node.draw});
		});
	}

	drawWires {
		wires.do({|wire|
			Pen.use({wire.draw});
		});
	}

	drawTempWire {
		if (wireIn != nil, {
			Pen.line(
				(wireIn.x)@(wireIn.y),
				mouseX@mouseY
			);
			Pen.width = 3;
			Pen.strokeColor = Color.gray;
			Pen.stroke;
		})
	}

	addNode {|node| nodes.add(node); }

	toolSelected {|value| selectedTool = value; }

	nodeTypeSelected {|type|
		selectedNodeType = type;
		"nodeTypeSelected: %".format(type).postln;
	}
}