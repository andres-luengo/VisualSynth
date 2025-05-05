VSNode {
	classvar nodeCount = 0;
	var <x, <y, <>color;
	var <>hovered;
	var <>selected;
	var <inputs;
	var <output;
	const size = 50;

	*new {
		|x, y, color = (Color(0.9, 0.9, 0.9))|
		var instance;
		instance = super.newCopyArgs(x, y, color).prVSNodeInit;
		^instance;
	}

	prVSNodeInit {
		inputs = 2.collect({|i|
			WirePort(this, x, y + this.prPortY(i, 2), \left)
		});
		output = WirePort(this, x + size, y + (size/2), \right);
		hovered = false;
		selected = false;
		^this;
	}

	draw {
		Pen.translate(x, y);
		Pen.use({
			this.drawBody;
		});
		Pen.use({
			this.drawPorts;
		});
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
		Pen.moveTo(0@0);
		Pen.use({
			inputs.do({ |port, i|
				Pen.translate(0, spacing);
				Pen.use({ port.draw });
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

	portAt { |wx, wy| // <- IN WORLD FRAME
		var nx = wx - x; // <- TO NODE FRAME
		var ny = wy - y;
		var px, py; // <- PORT FRAME
		inputs.do({|port, i|
			px = nx;
			py = ny - this.prPortY(i);

			// returns first hit... if ports on the same node overlap, i think there's bigger problems
			if (port.contains(px, py), {
				^port;
			});
		});

		px = nx - size;
		py = ny - (size / 2);
		if (output.contains(px, py), {
			^output;
		});
		^nil;
	}

	prPortY { |i, numInputs = (inputs.size)|
		var spacing = size / numInputs;
		^(spacing * (i + 0.5)); // <- NODE FRAME
	}

	x_ {|val|
		x = val;
		inputs.do({|port|
			port.x = val;
		});
		output.x = val + size;
	}

	y_ {|val|
		y = val;
		inputs.do({|port, i|
			port.y = this.prPortY(i)
		});
		output.y = val + size/2;
	}
}