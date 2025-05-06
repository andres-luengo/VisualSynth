To install, place a folder containing this source code into the folder returned by
```
Platform.userExtensionDir;
```
and recompile your libraries (CTRL/CMD + SHIFT + L).

# USAGE
```
v = VisualSynth.new;
```
Then, build a synth in the resulting window using the GUI elements. 
Make sure to have an Out node. Loops and Synth arguments are not supported (loops will lead to undefined behavior!). 
The next best thing for parameters is to put in Const nodes and double click them with the Edit tool to modify their values.

You can also modify the default values for the frequency, amplitude, and add of the SinOsc nodes by double clicking them with the Edit tool.

Once you're ready to hear your SinOsc spaghetti,
```
v.getSynthDef(\mySynth).add;

Synth(\mySynth);
```
