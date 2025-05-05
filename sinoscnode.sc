SinOscNode : VSNode {
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
}