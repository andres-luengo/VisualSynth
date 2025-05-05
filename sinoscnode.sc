SinOscNode : VSNode {
	var freq = 440;
	var amp = 1.0;
	var add = 0.0;

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
		amp = values[1].asFloat;
		add = values[2].asFloat;
    }
    
    openProperties {
        PropertiesWindow.new(
            properties: #["Frequency", "Amplitude", "Add"],
            initialValues: [freq, amp, add],
            doneCallback: { |values|
                var validateResult = this.prValidateValues(values);
                if (validateResult) {
                    this.prUpdateValues(values);
                };
                validateResult;
            }
        );
    }
}