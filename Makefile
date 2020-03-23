JC = javac
JVM = java
JFLAGS = -g
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	  src/launcher/launcher.java\
	  src/main/encrypt.java\
	  src/main/decrypt.java

MAIN = launcher
default:classes
classes: $(CLASSES:.java=.class)

run: $(MAIN).class
	$(JVM) $(MAIN)

clean:
	$(RM) *.class
