VisualSynthBuilder {
	var nodes, wires;
	*new {|nodes, wires|
		^super.newCopyArgs(nodes, wires);
	}

	prTopologicalSort {
		var outNode = nodes.detect {|node|
			node.class === OutNode
		};
		if (outNode.isNil) {
			"Graph must have an Out node to create a Synth.".error;
			MethodError.throw;
		};
		
	}

	getSynthDef {
		this.prTopologicalSort;
	}
}