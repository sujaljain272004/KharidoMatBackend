# Stage 1: Use a Maven image that has Java 22 to build the app
# We are now using an 'eclipse-temurin' image which is a high-quality distribution of Java
FROM maven:3.9.6-eclipse-temurin-22 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project's pom.xml to download dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of your project's source code
COPY src ./src

# Build the project and create the executable JAR file.
RUN mvn clean install -DskipTests

# --------------------------------------------------------------------

# Stage 2: Use a lightweight Java 22 image to run the app
# This stage creates the final, small container for deployment
FROM eclipse-temurin:22-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file that was created in the 'build' stage above.
COPY --from=build /app/target/kharidoMat-0.0.1-SNAPSHOT.jar app.jar

# Tell Render that our application will be listening for requests on port 10000.
EXPOSE 10000

# This is the final command that runs when the container starts.
CMD ["java", "-jar", "app.jar"]
