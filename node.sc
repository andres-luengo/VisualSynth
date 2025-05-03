SynthNode {
	var <>x, <>y, <>color;
	var output;
	const size = 50;

	*new {
		|x, y, color = (Color.cyan)|
		^super.newCopyArgs(x, y, color);
	}

	// draws a triangle :) 👍
	draw {
		Pen.translate(x, y);
		Pen.moveTo(0@0);

		Pen.fillColor = color;

		Pen.strokeColor = Color.gray;
		Pen.width = 2;

		Pen.lineTo(0@size);
		Pen.lineTo((size)@(size/2));
		Pen.lineTo(0@0);

		Pen.fillStroke;
	}
}