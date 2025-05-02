VisualSynth : Window {
	var name, bounds;
	var mainView;

	*new {|name = "Visual Synth", bounds = (Rect(200, 200, 800, 600)), resizable = false ... args|
		var instance = super.new(name, bounds, resizable, *args);
		instance.prVisualSynthInit;
		^instance;
	}

	prVisualSynthInit {
		this.bounds.postln;
		this.layout = VLayout(
			[Button().states_([["yes", Color.grey, Color.white], ["no", Color.white, Color.grey]])]
		);
		this.front;
		^this;
	}
}