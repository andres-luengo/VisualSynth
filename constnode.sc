ConstNode : VSNode {
    prInitPorts {
        inputs = []; // i'm sure this won't cause problems :)
        output = WirePort(this, x + size, y + (size/2), \right);
    }
}