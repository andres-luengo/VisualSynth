SynthNode {
	classvar nodeCount = 0;
	var <>x, <>y, <>color;
	var <>hovered;
	var <>selected;
	var output;
	const size = 50;

	*new {
		|x, y, color = (Color(0.9, 0.9, 0.9))|
		var instance;
		instance = super.newCopyArgs(x, y, color).prSynthNodeInit;
		^instance;
	}

	prSynthNodeInit {
		hovered = false;
		selected = false;
		^this;
	}

	draw {
		Pen.translate(x, y);
		Pen.moveTo(0@0);

		Pen.fillColor = color;

		if (selected, {
			Pen.strokeColor = Color.black;
		}, {
			Pen.strokeColor = Color.gray;
		});
		if (hovered || selected, {
			Pen.width = 4;
		}, {
			Pen.width = 2;
		});

		Pen.lineTo((size)@(size/2));
		Pen.lineTo(0@size);
		Pen.lineTo(0@0);

		Pen.fillStroke;
	}

	contains {|x, y|
		var boundingRect = Rect.new(this.x, this.y, size, size);
		var result = boundingRect.contains(x@y);
		^result;
	}
}