# School Management System Challenge

## Intro and Context

Welcome and thank you for participating in the "School Management System Challenge"!

You were hired as a Software Engineer to design and implement a new school management system for the country you live in.
A school management system is able to manage pupils and students as well as courses, grades, etc...
When the system will go live after completion it will be a highly distributed, scalable and fault tolerant system,
with local data sovereignty for each school. I.e. each school will host its own node/instance - you name it - of the system,
which stores the data of the specific school only in a local data center.


## Your Task

At the time being you have to develop a first "Proof of Concept" (PoC) iteration of the system.
Your first iteration will have a narrow scope, but the foundations should be laid to continuously 
expand the features and functions of the system.

Read the subsequent sections carefully and develop an application of the scope described in the "Proof of Concept Scope"
chapter. Comply with all requirements of the "Framing Conditions" section.
You already got some sample data that helps to develop and test you application.
Finally ensure that your application could be executed using the given test data.

Don't make your life harder than it is and make good use of the help available in the sections "Hints and Helpers" and
"Closing Remarks and Priorities".


## Proof of Concept Scope

### Terms

 * MUST -This word, or the terms “REQUIRED” or “SHALL”, mean that the definition is an absolute requirement of the specification.
 * SHOULD – This word, or the adjective “RECOMMENDED”, mean that there may exist valid reasons in particular circumstances to ignore a particular item, but the full 
            implications must be understood and carefully weighed before choosing a different course.
 * MAY – This word, or the adjective “OPTIONAL,” mean that an item is truly discretionary.
 * FUTURE – This word means that objectives are provided as guidance or expectation and may or may not be accurate.

### Functional Description

The initial system should consist of three components (Actors):
  1. Schools Management System Supervisor
  2. School(s)
  3. Student Data Importer

The - Schools Management System Supervisor - acts as a coordinator.
On request he must be able to add/instantiate new schools to the system (spawn child actors).
He also keeps track of the added/available/used schools and is able to return a corresponding list of schools.

A - School - is the digital twin of the actual physical entity. I.e. there exists one instance per actual school.
The school actor must act as a facade for all actions, operations and data on and about this school. I.e. it encapsulates the access
and the application logic regarding schools. In a future version this might include exchanging data between different schools.
Local administrators are already familiar in operating "NotSoFancySchoolDatabase" instances. The school actor must be able to read and write to this databases.

The - Student Data Importer - is important for testing purposes and before go-live of the system.
Test or historical data will be provided as a comma-separated values (CSV) file. The importer must be able to 
1) parse the given students data, 2) forward students data to the corresponding school actor for storage, 
and 3) report simple statistics on how many records have been processed and what the system state is after the import.

For the scope of the PoC it is sufficient to store the final grades per school without any students context to preserve the students privacy during early development.
Afterwards it must be possible (per school) to retrieve the average grade together with the amount of grades the average is based on.   


The PoC application (executable class, e.g. class with main method) should 1) bundle and start the aforementioned components and
2) execute a data import with the given test data. Finally it should - per school - 3) retrieve and report (print/log) the average grade together with the amount of grades the average is based on. A potential example output can be found in the "Hints and Helpers" section.

### Acceptance Criteria
The implementation is considered functional correct if the reported data matches the given results of the test data set
mentioned in the section "Hints and Helpers" and if it could be demonstrated that all imported test data 
has been first written and then retrieved from the "NotSoFancySchoolDatabase".


### Framing Conditions

* The application MUST be implemented in Scala.
* The application MAY use any Scala version.
* The application MAY use any Java version.
* The application design MUST be based on the "Actor Model" and must use the Akka library.
* The application MAY use any Akka version and may be implemented in either "Akka classic or Akka typed" style.
* The data import MAY be implemented with the help of stream processing (Akka Streams).
* The application MUST store and retrieve grade data using the provided "NotSoFancySchoolDatabase" implementation.
  The implementation of the "NotSoFancySchoolDatabase" MUST not be changed.
* Scala supports concepts from object-oriented as well as from functional programming. 
  Object-oriented principles shine for composition “in the large”, e.g., component architectures, whereas functional principles shine for composition “in the small”, 
  i.e. separation between (immutable) data and functions operating on data.
  It is recommended to apply a "functional style" where possible and appropriate.
  E.g., especially no (few) loops, no mutable variables, use of immutable data structures, etc.
* The application SHOULD log some information to a file/stdout/console to make the code execution and the interaction between components comprehensible.
* The application does not need any external interfaces beside reading from the CSV filed described.
* The application does not need any "User Interface". All information could be exchanged by Stdout (also known as standard output) or log files
* The application SHOULD not need any further libraries, have further dependencies than akka-actor(-typed), akka-stream(-typed), and a logging library

## Hints and Helpers

* Find the test data and a description of the test data columns in folder "20 - Test Data"
* "30 - Expected Results" contains a control calculation of the expected output in Excel 
        and a corresponding screenshot, in case excel is not available.
* "40 - Resources" contains the provided implementation of the "NotSoFancySchoolDatabase"
* "50 - Examples" contains an example of how the output of the PoC application could look like
* We recommend to use Intellij IDEA Community edition for Scala development
* We recommend to setup an sbt project. Maybe even starting with an existing example project.
  E.g. see https://doc.akka.io/docs/akka/current/typed/actors.html#first-example
       and https://github.com/akka/akka/blob/v2.6.19/akka-actor-typed-tests/src/test/scala/docs/akka/typed/IntroSpec.scala#L9-L11
* We recommend conservative decisions regarding versions to avoid time consuming non functional specification related error tackling 
  (e.g. stable released versions, long term support versions)
* The not so fancy school database is slow and a bit shaky. Expect the import to run for 30 to 60s.


# Closing Remarks and Priorities

You will present the results and especially the experience of the "School Management System Challenge"
in the next appointment.
Do not put too much effort in terms of time into the results of the exercise (not more than a couple of hours) unless it really feels like fun. 
We are more interested in what you can explain about your process, ideas, challenges, and how you would move forward with more time.
Being able to verbally describe how your "perfect solution" would look like is equally valid as long it becomes clear that the problem is understood well.
In case you are stuck - for too long - rather skip or fake a certain aspect/challenge than using too much time on it.

If you have any questions, please feel free to contact me (nwei@camelot-mc.com).
