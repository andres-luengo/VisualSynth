VisualSynth : Window {
	var name, bounds;
	var mainView;
	var sandbox;

	*new {|name = "Visual Synth", bounds = (Rect(100, 100, 800, 600)), resizable = false ... args|
		var instance = super.new(name, bounds, resizable, *args);
		instance.prVisualSynthInit;
		^instance;
	}

	prVisualSynthInit {
		this.acceptsMouseOver = true;
		sandbox = Sandbox();
		this.layout = VLayout(
			sandbox.uview
		);
		10.do({|i| sandbox.addNode(SynthNode(i * 10, i * 10)) });

		this.front;
		^this;
	}
}