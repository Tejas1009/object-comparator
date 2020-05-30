package object.comparator;


import models.AssociatedColumn;
import models.CustomColumn;
import models.CustomTable;
import models.Relation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectComparatorTest {
    private CustomTable ct1 = new CustomTable();
    private CustomTable ct2;

    @BeforeEach
    public void setup() throws CloneNotSupportedException {
        ct1.setName("customerobj");
        ct1.setPrimarycolumn("id");
        List<CustomColumn> lcc = new ArrayList<>();
        CustomColumn cc = new CustomColumn();
        cc.setName("id");
        cc.setLength(255);
        cc.setType("bigint");
        lcc.add(cc);
        ct1.setColumns(lcc);

        List<Relation> lr = new ArrayList<>();
        Relation r = new Relation();
        r.setName("customertypeobj");

        AssociatedColumn ac = new AssociatedColumn();
        ac.setDest("customer_type_internal_id");
        ac.setSrc("customer_type_internal_id");
        r.setAssociatedcolumn(ac);
        lr.add(r);
        ct1.setRelations(lr);
    }

    @Test
    public void shouldGetCustomTableNameDelta() throws CloneNotSupportedException {
        CustomTable ct2 = (CustomTable) ct1.clone();
        CustomColumn cc1 = new CustomColumn();
        cc1.setName("id");
        cc1.setLength(255);
        cc1.setType("bigint");
        List<CustomColumn> lcc1 = new ArrayList<>();
        lcc1.add(cc1);

        ct2.setName("dummy");
        List<ObjectComparator.Delta> deltas = ObjectComparator.compare(ct1, ct2);
        assertTrue(deltas.size() == 1);
        ObjectComparator.Delta delta = deltas.get(0);
        assertTrue("name".equals(delta.getFieldName()));
        assertEquals(CustomTable.class.getSimpleName(), delta.getParent());
    }

    @Test
    public void shouldGetNewAddedColumns() throws CloneNotSupportedException {
        ct1.setColumns(new ArrayList<>());
        CustomTable ct2 = (CustomTable) ct1.clone();
        List<CustomColumn> customColList = new ArrayList<>();

        CustomColumn exceptedCol1 = new CustomColumn();
        exceptedCol1.setName("id");
        exceptedCol1.setLength(255);
        exceptedCol1.setType("bigint");

        customColList.add(exceptedCol1);
        CustomColumn exceptedCol2 = new CustomColumn();
        exceptedCol2.setName("name");
        exceptedCol2.setLength(255);
        exceptedCol2.setType("varchar");
        customColList.add(exceptedCol2);
        ct2.setColumns(customColList);

        List<ObjectComparator.Delta> deltas = ObjectComparator.compare(ct1, ct2);
        assertTrue(deltas.size() == 2);
        ObjectComparator.Delta delta = deltas.get(1);
        assertTrue("columns".equals(delta.getFieldName()));
        CustomColumn actualCol1 = (CustomColumn) delta.getTargetValue();
        assertEquals(exceptedCol1.getName(), actualCol1.getName());
        assertEquals(exceptedCol1.getLength(), actualCol1.getLength());
        assertEquals(exceptedCol1.getType(), actualCol1.getType());
        assertEquals(CustomTable.class.getSimpleName(), delta.getParent());
        assertEquals(ObjectComparator.Operation.add, delta.getOperation());

        delta = deltas.get(0);
        CustomColumn actualCol2 = (CustomColumn) delta.getTargetValue();
        assertEquals(exceptedCol2.getName(), actualCol2.getName());
        assertEquals(exceptedCol2.getLength(), actualCol2.getLength());
        assertEquals(exceptedCol2.getType(), actualCol2.getType());
        assertEquals(CustomTable.class.getSimpleName(), delta.getParent());
        assertEquals(ObjectComparator.Operation.add, delta.getOperation());
    }

    @Test
    public void shouldGetRemovedSourceColumns() throws CloneNotSupportedException {
        CustomTable ct2 = (CustomTable) ct1.clone();
        List<CustomColumn> customColList = new ArrayList<>();
        ct2.setColumns(customColList);

        List<ObjectComparator.Delta> deltas = ObjectComparator.compare(ct1, ct2);
        assertTrue(deltas.size() == 1);
        ObjectComparator.Delta delta = deltas.get(0);
        assertTrue("columns".equals(delta.getFieldName()));
        CustomColumn actualCol1 = (CustomColumn) delta.getSrcValue();
        assertEquals(ct1.getColumns().get(0).getName(), actualCol1.getName());
        assertEquals(ct1.getColumns().get(0).getLength(), actualCol1.getLength());
        assertEquals(ct1.getColumns().get(0).getType(), actualCol1.getType());
        assertEquals(CustomTable.class.getSimpleName(), delta.getParent());
        assertEquals(ObjectComparator.Operation.remove, delta.getOperation());
    }

    @Test
    public void shouldGetAddedRelations() throws CloneNotSupportedException {
        CustomTable ct2 = (CustomTable) ct1.clone();
        List<Relation> lr = new ArrayList<>();
        Relation r = new Relation();
        r.setName("customertypeobj");

        AssociatedColumn ac = new AssociatedColumn();
        ac.setDest("customer_type_id");
        ac.setSrc("customer_type_id");
        r.setAssociatedcolumn(ac);
        lr.add(r);
        ct2.setRelations(lr);


        List<ObjectComparator.Delta> deltas = ObjectComparator.compare(ct1, ct2);
        assertTrue(deltas.size() == 2);
        ObjectComparator.Delta delta = deltas.get(1);
        assertTrue("relations".equals(delta.getFieldName()));
        Relation actualCol1 = (Relation) delta.getTargetValue();
        assertEquals(ac.getSrc(), actualCol1.getAssociatedcolumn().getSrc());
        assertEquals(ac.getDest(), actualCol1.getAssociatedcolumn().getDest());
        assertEquals(ObjectComparator.Operation.add, delta.getOperation());

        delta = deltas.get(0);
        assertTrue("relations".equals(delta.getFieldName()));
        Relation actualCol2 = ct1.getRelations().get(0);
        assertEquals(ct1.getRelations().get(0).getAssociatedcolumn().getSrc(), actualCol2.getAssociatedcolumn().getSrc());
        assertEquals(ct1.getRelations().get(0).getAssociatedcolumn().getDest(), actualCol2.getAssociatedcolumn().getDest());
        assertEquals(ObjectComparator.Operation.remove, delta.getOperation());
    }

    @Test
    public void shouldGetAddedColumnAndRelation() {
        CustomTable ct2 = new CustomTable();
        ct2.setName("customerobj");
        ct2.setPrimarycolumn("id");
        List<CustomColumn> lcc = new ArrayList<>();
        CustomColumn cc = new CustomColumn();
        cc.setName("id");
        cc.setLength(255);
        cc.setType("bigint");
        lcc.add(cc);
        ct2.setColumns(lcc);

        CustomColumn cc2 = new CustomColumn();
        cc2.setName("name");
        cc2.setLength(255);
        cc2.setType("varchar");
        lcc.add(cc2);
        ct2.setColumns(lcc);

        List<Relation> lr = new ArrayList<>();
        Relation r = new Relation();
        r.setName("customertypeobj");

        AssociatedColumn ac = new AssociatedColumn();
        ac.setDest("customer_type_internal_id");
        ac.setSrc("customer_type_internal_id");
        r.setAssociatedcolumn(ac);
        lr.add(r);

        Relation r2 = new Relation();
        r2.setName("dummytable");

        AssociatedColumn ac2 = new AssociatedColumn();
        ac2.setDest("dummy_col");
        ac2.setSrc("dummy_col");
        r2.setAssociatedcolumn(ac2);
        lr.add(r2);

        ct2.setRelations(lr);

        List<ObjectComparator.Delta> deltas = ObjectComparator.compare(ct1, ct2);
        assertTrue(deltas.size() == 2);
        ObjectComparator.Delta delta = deltas.get(0);
        assertTrue("relations".equals(delta.getFieldName()));
        Relation actualRelation = (Relation) delta.getTargetValue();
        assertEquals(ct2.getRelations().get(1).getAssociatedcolumn().getSrc(), actualRelation.getAssociatedcolumn().getSrc());
        assertEquals(ct2.getRelations().get(1).getAssociatedcolumn().getDest(), actualRelation.getAssociatedcolumn().getDest());
        assertEquals(ObjectComparator.Operation.add, delta.getOperation());

        delta = deltas.get(1);
        assertTrue("columns".equals(delta.getFieldName()));
        CustomColumn actualCol1 = (CustomColumn) delta.getTargetValue();
        assertEquals(ct2.getColumns().get(1).getName(), actualCol1.getName());
        assertEquals(ct2.getColumns().get(1).getLength(), actualCol1.getLength());
        assertEquals(ct2.getColumns().get(1).getType(), actualCol1.getType());
        assertEquals(CustomTable.class.getSimpleName(), delta.getParent());
        assertEquals(ObjectComparator.Operation.add, delta.getOperation());
    }
}