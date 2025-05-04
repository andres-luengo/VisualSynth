VisualSynth : Window {
	var name, bounds;
	var mainView;
	var sandbox, toolSelect;

	*new {|name = "Visual Synth", bounds = (Rect(100, 100, 800, 600)), resizable = false ... args|
		var instance = super.new(name, bounds, resizable, *args);
		instance.prVisualSynthInit;
		^instance;
	}

	prVisualSynthInit {
		var toolBar;
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

		toolBar = HLayout(toolSelect);

		this.layout = VLayout(
			[toolBar, alignment: \top],
			[sandbox.uview, alignment: \bottom]
		);

		this.front;
		^this;
	}
}