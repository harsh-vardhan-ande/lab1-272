package edu.sjsu.lab1272;

import java.util.Date;

/*
    Define entity structures for the graphql query
    outputs received from CanvasClient
 */
class Term{
    public String name;
    public  Term() {}
    public Term(String name) {
        this.name = name;
    }
}
class Course {

    public String _id;
    public String name;
    public Term term;

    public Course() { }

    public Course(String _id, String name, Term term) {
        this._id = _id;
        this.name = name;
        this.term = term;
    }
}

class Assignment {
    public String _id;
    public String name;
    public Date dueAt;
    public Assignment(){}
    public Assignment(String _id, String name, Date dueAt){
        this._id = _id;
        this.name = name;
        this.dueAt = dueAt;
    }
}
class AssignmentsConnection {
    public Assignment[] nodes;
    public AssignmentsConnection(){}
    public AssignmentsConnection(Assignment[] nodes){
        this.nodes = nodes;
    }
}
class CourseAssignment {
    public String id;
    public String _id;
    public String name;
    public AssignmentsConnection assignmentsConnection;

    public CourseAssignment(){}
    public CourseAssignment(String id, String _id, String name, AssignmentsConnection assignmentsConnection){
        this.id = id;
        this._id = _id;
        this.name = name;
        this.assignmentsConnection = assignmentsConnection;
    }
}
