VisualSynth : Window {
	classvar nodeTypes;
	classvar instance;
	var name, bounds;
	var mainView;
	var sandbox, toolSelect, nodeSelect;

	*new {|name = "Visual Synth", bounds = (Rect(100, 100, 800, 600)), resizable = false ... args|
		if (instance.isNil.not) {
			instance.close;
		};
		instance = super.new(name, bounds, resizable, *args);
		if (nodeTypes.isNil) {
			nodeTypes = Dictionary.newFrom([
				Out: OutNode,
				SinOsc: SinOscNode,
				Const: ConstNode
			]);
		};
		instance.prVisualSynthInit;
		^instance;
	}

	prVisualSynthInit {
		var toolbar;
		this.acceptsMouseOver = true;

		sandbox = Sandbox();

		toolSelect = ListView();
		toolSelect.items = [
			\Edit,
			\Wire,
			\Node,
			\Delete
		];
		toolSelect.action = {
			var selectedTool = toolSelect.items[toolSelect.value];
			sandbox.toolSelected(selectedTool);
		};
		toolSelect.action.value; // run once to set selected tool
		toolSelect.maxHeight = (toolSelect.items.size * 18);

		nodeSelect = ListView();
		nodeSelect.items = [
			\Out,
			\SinOsc,
			\Const
		];
		nodeSelect.action = {
			var selectedNodeTypeName = nodeSelect.items[nodeSelect.value];
			var selectedNodeType = nodeTypes[selectedNodeTypeName];
			sandbox.nodeTypeSelected(selectedNodeType);
		};
		nodeSelect.action.value;
		nodeSelect.maxHeight = (nodeSelect.items.size * 18);

		toolbar = HLayout(toolSelect, nodeSelect);

		this.layout = VLayout(
			[toolbar, alignment: \top],
			[sandbox.uview, alignment: \bottom]
		);

		this.onClose = {
			sandbox.close;
			instance= nil;
		};
		this.front;
		^this;
	}

	getSynthDef {|name = \visualsynth|
		var outNode = OutNode.instance;
		if (outNode.isNil) {
			"Need at least one Out node to make a SynthDef.".error;
			^nil;
		};
		^SynthDef(name, { |out| outNode.getUGen(out); });
	}
}