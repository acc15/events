events
======

Java Listener framework

More detailed description will be ready soon

import static ru.vmsoftware.events.Events.*;

emit(this, KeyboardEvent.KEY_DOWN, keyCode);


Emitters

Emitter is a holder of real emitter instance and serves
for simplifying emitting events especially when emitting from inner classes.

Was:

  public void doWork() {
    emit(this, MyEvent.EVENT_1);
    // ... do some work ...
    emit(this, MyEvent.EVENT_5, data);
  }

Become:

  void init() {
    // ... somewhere in initialization code ...
    emitter = Events.emitter(this).event(MyEvent.EVENT_1, data);
  }

  public void doWork() {

    // process implementation
    emitter.emit(event(MyEvent.EVENT_1));
    // ... do some work ...
    emitter.emit(event(MyEvent.EVENT_5, data));
  }