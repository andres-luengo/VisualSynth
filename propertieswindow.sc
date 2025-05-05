PropertiesWindow : Window {
    var properties; // Iterable[Name]
    var textFields; // Iterable[textField]
    var doneCallback;

    *new { |properties, initialValues, doneCallback = ({|props| true}), name = "Properties" ... args|
        var instance;
        instance = super.new(name, *args);
        instance.prPropertiesInit(properties, initialValues, doneCallback);
        ^instance;
    }

    prOKButton {
        var button = Button.new;
        button.states_([
            ["OK"]
        ]);
        button.action = { this.prFinish; };
        ^button;
    }

    prPropertiesInit {|props, initValues, cBack|
        var propLayout;
        properties = props;
        doneCallback = cBack;
        textFields = Array.fill(properties.size, { nil; });

        propLayout = VLayout.new(
            *properties.collect { |propName, i|
                var textField;
                textField = TextField.new;
                if (initValues.isNil.not) {textField.value_(initValues[i])};
                textFields[i] = textField;

                HLayout.new(
                    StaticText.new.string_(propName),
                    textField
                );
            }
        );

        this.layout = VLayout.new(
            propLayout,
            this.prOKButton;
        );
        this.setInnerExtent(200, (properties.size + 1) * 20);
        this.front;
    }

    prFinish {
        var doneResult = doneCallback.value(
            textFields.collect { |field| field.value }
        );
        if (doneResult) {
            this.close;
        } {
            // i'm sure there's a more elegant way to do this but that's way 
            // too much work for something that isn't quite the point of this
            // project
            "INVALID INPUT".error;
        };
    }
}