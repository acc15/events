Events
======

Java Listener framework

More detailed description will be ready soon

import static ru.vmsoftware.events.Events.*;

emit(this, KeyboardEvent.KEY_DOWN, keyCode);

Registrars


Emitters

Emitter is a holder of real emitter instance and allow to simplify event emitting,
especially when emitting from inner classes.

Was:

    public void doWork() {
        emit(this, MyEvent.EVENT_1);
        // ... do some work ...
        emit(this, MyEvent.EVENT_5, data);
    }

Become:

    void init() {
        // ... somewhere in initialization code ...
        emitter = Events.emitter(this);
    }

    public void doWork() {
        // process implementation
        emitter.emit(MyEvent.EVENT_1);
        // ... do some work ...
        emitter.emit(MyEvent.EVENT_5, data);
    }


Thread safety:

Currently, Events isn't thread safe but there is a plans to make it.