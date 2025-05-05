WirePort {
	var <node; // VSNode
	var <>x, <>y; // float? int? whatever
	var <dir; // \left or \right
	var <>wires; // List[Wire]
	var <>hovered; // bool
	var drawRect, hitRect; // Rect
	const drawLen = 5;
	const drawWidth = 10;
	const hitboxSizeMult = 1.5;

	*new {|node, x, y, dir = \left|
		var instance = super.newCopyArgs(node, x, y, dir);
		instance.prWirePortInit;
		^instance;
	}

	prWirePortInit {
		var leftEnd;
		hovered = false;

		if (dir == \left, {
			leftEnd = drawLen.neg;
		}, {
			leftEnd = 0;
		});

		drawRect = Rect(
			leftEnd, (drawWidth / 2).neg,
			drawLen, drawWidth
		);
		hitRect = Rect(
			leftEnd * hitboxSizeMult, (drawWidth / 2).neg * hitboxSizeMult,
			drawLen * hitboxSizeMult, drawWidth * hitboxSizeMult
		);

		wires = List.new;
	}

	draw {
		// assume a transform was made such that 0@0 is the connection point
		Pen.moveTo(0@0);
		if(hovered, {
			Pen.addRect(hitRect);
			Pen.fillColor = Color.gray;
			Pen.width = 2;
		}, {
			Pen.addRect(drawRect);
			Pen.fillColor = Color.white;
			Pen.width = 1;
		});

		Pen.strokeColor = Color.black;
		Pen.fillStroke;
	}

	contains {|x, y|
		// IN PORT FRAME
		^hitRect.contains(x@y);
	}
}