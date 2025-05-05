// so that I can have nodes with either no output/input
NullWirePort : WirePort {
	*new { ^super.new; }

	draw {}
	contains { ^false; }

	clearWires {}

	// want to know if code ever tries screwing with wires
	wires {       DoesNotUnderstandError.throw; }
	wires_{ |val| DoesNotUnderstandError.throw; }
}