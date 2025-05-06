ConstNode : VSNode {
    var constValue = 0.0;

    prInitPorts {
        inputs = []; // i'm sure this won't cause problems :)
        output = WirePort(this, x + size, y + (size/2), \right);
    }

    prValidateValues {|values|
        if (values.size != 1) {
            ^false;
        };
        if ("^-?\\d+(\\.\\d+)?$".matchRegexp(values[0]).not) {
            ^false;
        }
        ^true;
    }
    
    prUpdateValues {|values|
        constValue = values[0].asFloat;
        view.refresh;
    }
    
    openProperties {
        PropertiesWindow.new(
            properties: #["Value"],
            initialValues: [constValue],
            doneCallback: { |values|
                var validateResult = this.prValidateValues(values);
                if (validateResult) {
                    this.prUpdateValues(values);
                };
                validateResult;
            }
        );
    }

    drawBody {
        super.drawBody;
        Pen.fillColor = Color.black;
        Pen.font = Font("Helvetica-Bold", 12);
        Pen.stringCenteredIn(
            constValue.asStringPrec(5),
            Rect.new(0, 0, size, size)
        );
    }

    getUGen {
        ^Mix.ar(constValue);
    }
}   