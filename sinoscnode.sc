SinOscNode : VSNode {
	var freq = 440;
	var mul = 1.0;
	var add = 0.0;

	prInitNodes {
		inputs = [
			WirePort(this, x, y + this.prPortY(0, 2), \left, "FREQ"),
			WirePort(this, x, y + this.prPortY(1, 2), \left, "MUL")
		];
		output = WirePort(this, x + size, y + (size/2), \right);
	}

	drawBody {
		Pen.moveTo(0@0);

		Pen.fillColor = Color.cyan;

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

		Pen.lineTo(size@(size/2));
		Pen.lineTo(0@size);
		Pen.lineTo(0@0);


		Pen.fillStroke;
	}

	prValidateValues {|values|
        if (values.size != 3) {
            ^false;
        };
        if (values.any {|value| "^-?\\d+(\\.\\d+)?$".matchRegexp(value).not}) {
            ^false;
        };
        ^true;
    }
    
    prUpdateValues {|values|
        freq = values[0].asFloat;
		mul = values[1].asFloat;
		add = values[2].asFloat;
    }
    
    openProperties {
        PropertiesWindow.new(
            properties: #["Frequency", "Amplitude", "Add"],
            initialValues: [freq, mul, add],
            doneCallback: { |values|
                var validateResult = this.prValidateValues(values);
                if (validateResult) {
                    this.prUpdateValues(values);
                };
                validateResult;
            }
        );
    }

	getUGen {
		^SinOsc.ar(
			freq: this.inSignal(0, freq),
			mul: this.inSignal(1, mul),
			add: add
		);
	}
}