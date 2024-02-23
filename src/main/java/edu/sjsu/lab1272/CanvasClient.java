package edu.sjsu.lab1272;

import java.util.List;

import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/*
    Retrieval of data using graphql client of spring was done how the official documentation suggests to use it.
    https://docs.spring.io/spring-graphql/reference/client.html#client.requests
 */
public class CanvasClient {
    private String token;
    private RestTemplate restTemplate;

    private HttpGraphQlClient getGraphQLClient(String token) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://sjsu.instructure.com/api/graphql")
                .build();
        HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(webClient)
                .headers(headers -> headers.setBearerAuth(token))
                .build();
        return graphQlClient;
    }

    public List<Course> ListCourses(String token) {
        HttpGraphQlClient graphQLClient = this.getGraphQLClient(token);
        try {
            return graphQLClient.document("""
                            query MyQuery {
                               allCourses {
                                 _id
                                 name
                                 term {
                                   name
                                 }
                               }
                             }
                            """).retrieve("allCourses")
                    .toEntityList(Course.class).block();
        } catch (GraphQlTransportException exception) {
            System.out.printf("GraphQlTransportException: %s\n", exception.getMessage());
        }
        return null;
    }

    public CourseAssignment ListAssignments(String token, String courseId) {
        HttpGraphQlClient graphQLClient = this.getGraphQLClient(token);
        try {
            return graphQLClient.document(String.format(
                            """
                                    query MyQuery {
                                      course(id: "%s") {
                                        id
                                        name
                                        _id
                                        assignmentsConnection {
                                          nodes {
                                            _id
                                            name
                                            dueAt
                                          }
                                        }
                                      }
                                    }
                                    """,
                            courseId
                    )).retrieve("course")
                    .toEntity(CourseAssignment.class).block();
        } catch (GraphQlTransportException exception){
            System.out.printf("GraphQlTransportException: %s\n", exception.getMessage());
        }
        return null;
    }
}
