# Coverage Analysis and White-box Testing

The objective of this exercise is the application of methods for analyzing the test coverage in order to design white-box test cases.  

## Prerequisites

- [x] This exercise is based on your unit tests for the class _RingBuffer_ you wrote as part of _Assignment 01_. Continue working in the already existing repository!
- [x] Add this file _README2.md_ to your exising repository and follow the instructions below.
- [x] Check/update your `pom.xml` with all extensions required for this assignment as provided togehter with this readme file on the e-learning platform.


### Instructions

You have already developed test cases for the class _RingBuffer_ in the previous assignment. Before you continue working on these test cases, you should revise and update them according to the feedback you received in the discussion of the assignment in the lecture. Make sure your test cases are in conformance to best practices (see: [JUnit best practices][JUnit best practices 2] and [recommendations][JUnit best practices 2]).
Furthermore, if you detected any faults in the _RingBuffer_, make sure to fix the implementation so that your tests all pass green.

- Revise the test cases according to the feedback and fix the _RingBuffer_ implementation so all tests pass
- Make sure your tests are in conformance to guidelines and best practices on how to write unit tests with JUnit
- Push your changes to your upstream repository on GitHub and [create a release][GitHub creating releases]


## Creating White-box Test Cases _(2 points)_

Extend your existing test cases by white-box tests in order to **cover all instructions and all branches** of the implementation of the RingBuffer. 

In order to run a coverage analysis, the `pom.xml` has to be extended by adding the _jacoco-maven-plugin_ to the section 
_build/plugins_. 

```
			<plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.11</version>
				<executions>
					<execution>
						<id>jacoco-prepare-agent</id>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>jacoco-report</id>
						<phase>test</phase>
						<goals>
							<goal>report</goal>
						</goals>
					</execution>				
				</executions>
			</plugin>
```


### Instructions

First, measure the coverage of the test cases you previously wrote/reviesed for the RingBuffer. Then, write additional test cases until a full coverage has been achieved. 

- Run your tests with _mvn test_ to perform the coverage analysis. The generated coverage report is stored in the directory _target/site/jacoco_. Use the report to identify the parts of the code that are not yet covered by your tests.
- Create a new test class _RingBufferWhiteboxTest_ containing further test cases in order to achieve full instruction (statement) and branch coverage of the class _RingBuffer_. 
- Note: For your convenience the `pom.xml` contains a commented-out execution configuration for jacoco that fails the build if instruction or branch coverage is less than 100%. Fell free to use this configuration to check for full coverage.
- When you're done, push your changes to your upstream repository on GitHub and [create a release][GitHub creating releases] 


## Mutation Analysis _(3 point)_

Mutation analysis makes small changes to the tested code in order to simulate artificial defects. This changed code is called a "mutant" of the original program. When the mutants are tested with the existing test cases, the number of seeded defects found ("killed mutants") indicates the quality of the tests. 

Use the mutation tool PIT (http://pitest.org/) to complete the exercise. Add the PIT plugin by extending the section 
_build/plugins_ in the `pom.xm` file. 

```
			<plugin>
				<groupId>org.pitest</groupId>
				<artifactId>pitest-maven</artifactId>
				<version>>1.15.1</version>
				<dependencies>
					<dependency>
						<groupId>org.pitest</groupId>
						<artifactId>pitest-junit5-plugin</artifactId>
						<version>1.2.0</version>
					</dependency>
				</dependencies>
				<executions>
					<execution>
						<id>pitest-mutation-coverage</id>
						<phase>test</phase>
						<goals>
							<goal>mutationCoverage</goal>
						</goals>					
					</execution>
				</executions>
			</plugin>
```

### Instructions

- Determine the mutation score for the existing tests by running `mvn test`. The generated HTML output can be found in the folder _target/pit-reports_. 
- Create a new test class RingBufferMutationTest and further test cases to achieve a mutation score of 100%.
- Note: For your convenience the `pom.xml` contains a commented-out execution configuration for pit that fails the build if the mutation score is less than 100%. Fell free to use this configuration to check for full mutation coverage.
- When you're done, push your changes to your upstream repository on GitHub and [create a release][GitHub creating releases] 


### Submission Procedure

When you're done...

- [x] push your changes to your upstream repository on GitHub
- [x] on GitHub, [create a release][GitHub creating releases] 
- [x] upload the [link to your release][GitHub linking to releases] on the e-learning platform until the specified date and time before the next lecture.


[GitHub creating releases]: https://help.github.com/articles/creating-releases/
[GitHub linking to releases]: https://help.github.com/articles/linking-to-releases/

[JUnit best practices 1]: https://www.baeldung.com/java-unit-testing-best-practices
[JUnit best practices 2]: https://phauer.com/2019/modern-best-practices-testing-java/