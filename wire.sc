Wire {
	var <in; // WirePort
	var <out; // WirePort

	*new {|in, out|
		var instance;
		instance = super.newCopyArgs(in, out).prWireInit;
		^instance;
	}

	prWireInit {
		in.wires.add(this);
		out.wires.add(this);
	}

	draw {
		Pen.moveTo(0@0);
		Pen.line(
			(in.x)@(in.y),
			(out.x)@(out.y),
		);
		Pen.width = 1;
		Pen.strokeColor = Color.black;
		Pen.stroke;
	}

	delete {
		in.wires.remove(this);
		out.wires.remove(this);
	}
}