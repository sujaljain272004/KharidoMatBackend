# Stage 1: Use an official Maven image to build the app
# This stage creates the .jar file inside a temporary container
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project's pom.xml to download dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of your project's source code
COPY src ./src

# Build the project and create the executable JAR file.
# We skip tests because they require a database connection which isn't available during this build step.
RUN mvn clean install -DskipTests

# --------------------------------------------------------------------

# Stage 2: Use a lightweight Java image to run the app
# This stage creates the final, small container for deployment
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file that was created in the 'build' stage above.
# It is copied from '/app/target/kharidoMat-0.0.1-SNAPSHOT.jar' inside the build container
# and renamed to 'app.jar' in our final container for simplicity.
COPY --from=build /app/target/kharidoMat-0.0.1-SNAPSHOT.jar app.jar

# Tell Render (or any other hosting service) that our application will be listening for requests on port 10000.
EXPOSE 10000

# This is the final command that runs when the container starts.
# It simply executes the Java application.
CMD ["java", "-jar", "app.jar"]
