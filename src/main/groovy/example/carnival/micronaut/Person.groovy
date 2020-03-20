package example.carnival.micronaut



import groovy.transform.ToString

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.apache.tinkerpop.gremlin.structure.Vertex

import carnival.core.graph.Core



/** A POJO representing a Person */
@ToString(includeNames=true)
class Person {
    static Person create(Vertex v) {
        assert v
        assert Core.PX.NAME.of(v).isPresent()
        assert GraphModel.PX.ID.of(v).isPresent()

        Person p = new Person()
        p.label = v.label()
        p.name = Core.PX.NAME.valueOf(v)
        p.id = GraphModel.PX.ID.valueOf(v)
        p
    }
    String label = GraphModel.VX.PERSON.label
    String id
    String name
}
