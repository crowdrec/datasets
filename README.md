datasets
========

| - DATA PROVIDER (e.g. Twitter)   
  | - TRANSFORMATION SCRIPT   
  | - DATASETS   
    | - DATASET X (e.g. May 2014 Twitter)   
      | - ORIGINAL RAW DATA (optional)   
      | - CROWDREC DATA   
      | Config File   

data model format
=================

## Motivation
The data model is inspired by multi-graphs consisting of nodes and edges.
The multi-graph structure allows the efficient representation of any relation between nodes.
The data model supports complex annotations for nodes and edges stored as JSON objects.
In order to support temporal changes (e.g, in stream-based scenarios), every node and edges is annotated with a timestamp.

## file structure
The data files consist of tap-separated fields. Typically 5 columns are used.

### Entities
`entityType <TAB> entityID <TAB> timestamp <TAB> properties  <TAB> linked-entities`

### Relations
`relationType <TAB> relationID <TAB> timestamp <TAB> properties  <TAB> linked-entities`

### data types
1. `entityType` and `relationType` are strings describing the type of the node/relation. Examples for entityType: `user`, `movie`. Examples for relationType: `rating.explicit`
2. `entityID` and `relationID` are string values.
3. `timestamp` is a long value based on the Unix epoch time. The timestamps indicates when the entity/relation has been created. If the timestamp is empty, the information is always available. 
4. `properties` is JSON-formatted string describing the properties of the entity/relation. Example for the properties of a `user` entity: `{name:"John Doe`, gender:"male"}`
5. `linked-entities` is a JSON-formatted string describing the linked entities. The linked entities are defined by the entity type and the entityID. Example for the linked entities of a `movie` entity: `{actors:["person:3001", "person:3006", "person:3486"], director:["person:3862"]}`

If no properties or linked entities are available, the columns 4 or 5 might be empty.

## Example
We show how a `user`-`rating`-`movie` dataset can be represented based on the data model.
At first, we define three persons.
Then we define a movie and link the persons with the persons.
Subsequently, we define a user and define a relation `rating.explicit` between the user and the movie.

`person <TAB> 3001<TAB> <TAB> {gender:"male",name:"Travolta, John"} <TAB>`
`person <TAB> 3004<TAB> <TAB> {gender:"male",name:"Jackson, Samuel"} <TAB>`
`person <TAB> 3003<TAB> <TAB> {gender:"male",name="Tarantino, Quentin"} <TAB>`
`movie<TAB>2202<TAB>129121892189<TAB>{title:"Pulp Fiction",year:"1994"}<TAB>{actors:["person:3001","person:3004"],director:"person:3003"}`
`user<TAB>1002<TAB>129121892189<TAB>{twitterId:"177651718",gender:"male",city:"Barcelona"} <TAB>`
`rating.explicit <TAB> 1001 <TAB> 129121892189 <TAB> {rating:5} <TAB> {subject:"user:1002",object:"movie:2202"}`

## Importing the data files
Since the file structure relies on tab-separated values (TSV/CSV) and JSON standard parsers can be used for reading the data files.
In order to import the data with `JAVA` the [Apache Commons CSV project](http://commons.apache.org/proper/commons-csv/ "Commons CSV Home") can be used.

*Limitations:*  In order to ensure that the columns in the file can be properly separated, tab-characters in text fields must be protected/escaped. Most csv parsers support `quoting` and `escaping`.
