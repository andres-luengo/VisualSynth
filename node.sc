VSNode {
	classvar nodeCount = 0;
	var <>x, <>y, <>color;
	var <>hovered;
	var <>selected;
	var inputs;
	var output;
	const size = 50;

	*new {
		|x, y, color = (Color(0.9, 0.9, 0.9))|
		var instance;
		instance = super.newCopyArgs(x, y, color).prVSNodeInit;
		^instance;
	}

	prVSNodeInit {
		inputs = [WirePort(this), WirePort(this)];
		output = WirePort(this, \right);
		hovered = false;
		selected = false;
		^this;
	}

	draw {
		Pen.translate(x, y);
		this.drawBody;
		this.drawPorts;
	}

	drawBody {
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

		Pen.lineTo(size@0);
		Pen.lineTo(size@size);
		Pen.lineTo(0@size);
		Pen.lineTo(0@0);

		Pen.fillStroke;
	}

	drawPorts {
		var spacing = size / (inputs.size + 1);
		Pen.use({
			inputs.do({ |port|
				Pen.translate(0, spacing);
				Pen.use({ port.draw })
			})
		});
		Pen.use({
			Pen.translate(size, size/2);
			output.draw;
		})
	}

	contains {|x, y|
		var boundingRect = Rect.new(this.x, this.y, size, size);
		var result = boundingRect.contains(x@y);
		^result;
	}
}