OutNode : VSNode {
	classvar <instance;

	*new {|...args|
		if (instance.isNil.not) {
			"May not create more than one Out node.".error;
			MethodError.throw;
		};
		instance = super.new(*args);
		^instance;
	}

	delete {
		instance = nil;
		^super.delete;
	}

	drawBody {
		Pen.moveTo(0@0);

		Pen.fillColor = Color.blue;

		if (selected) {
			Pen.strokeColor = Color.black;
		} {
			Pen.strokeColor = Color.gray;
		};
		if (hovered || selected) {
			Pen.width = 4;
		} {
			Pen.width = 2;
		};

		Pen.lineTo((size/2)@0);
		Pen.addArc((size/2)@(size/2), size/2, -pi/2,pi);
		// Pen.moveTo((size/2)@(size));
		Pen.lineTo(0@size);
		Pen.lineTo(0@0);


		Pen.fillStroke;
	}

	prInitPorts {
		inputs = [WirePort(this, x, y + (size/2), \left)];
		output = NullWirePort.new;
	}

	getUGen {|out|
		^Out.ar(out, Pan2.ar(this.inSignal(0, 0))).poll;
	}
}