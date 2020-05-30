package object.comparator;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import utilities.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ObjectComparator {
    public static final String ROOT = "-root-";

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Delta {
        private String fieldName;
        private Object srcValue;
        private Object targetValue;
        private Operation operation;
        private String parent;
    }

    public enum Operation {
        none,
        add,
        edit,
        remove
    }

    public static List<Delta> compare(Object source, Object target) {
        List<Delta> deltas = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        LinkedList<Delta> stack = new LinkedList<>();
        stack.push(new Delta(ROOT, source, target, Operation.none, source.getClass().getSimpleName()));

        while (!stack.isEmpty()) {
            Delta delta = stack.pop();
            final Object srcValue = delta.srcValue;
            final Object targetValue = delta.targetValue;
            if (srcValue == targetValue) {   // Same instance is always equal to itself.
                continue;
            }
            if (srcValue == null || targetValue == null) {   // If either one is null, they are not equal (both can't be null, due to above comparison).
                deltas.add(delta);
                continue;
            }
            if (isLogicalPrimitive(srcValue.getClass())) {
                if (!srcValue.equals(targetValue)) {
                    delta.operation = Operation.edit;
                    deltas.add(delta);
                }
                continue;
            }

            if (srcValue instanceof List) {
//                final Collection<Field> fields = ((List) srcValue).isEmpty() ? ReflectionUtils.getDeepDeclaredFields(((List) delta.getTargetValue()).get(0).getClass()) :
//                        ReflectionUtils.getDeepDeclaredFields(((List) delta.getSrcValue()).get(0).getClass());
//                for (Field field : fields) {
//                    if (!isLogicalPrimitive(field.getType())) {
//                        try {
//                            stack.push(new Delta(field.getName(), delta.getSrcValue(), delta.getTargetValue(), Operation.none, field.getType().getSimpleName()));
//                        } catch (Exception ignored) {
//                            System.out.println("\nError while pushing into stack");
//                        }
//                    }
//                }
                compareLists(delta, deltas);
                continue;
            }

            final Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(srcValue.getClass());
            for (Field field : fields) {
                try {
                    stack.push(new Delta(field.getName(), field.get(srcValue), field.get(targetValue), Operation.none, source.getClass().getSimpleName()));
                } catch (Exception ignored) {
                }
            }
        }
        return deltas;
    }

    /**
     * @return boolean true if the passed in object is a 'Logical' primitive.  Logical primitive is defined
     * as all primitives plus primitive wrappers, String, Date, Calendar, Number, or Character
     */
    private static boolean isLogicalPrimitive(Class<?> c) {
        return c.isPrimitive() ||
                String.class == c ||
                Date.class.isAssignableFrom(c) ||
                Number.class.isAssignableFrom(c) ||
                Boolean.class.isAssignableFrom(c) ||
                Calendar.class.isAssignableFrom(c) ||
                TimeZone.class.isAssignableFrom(c) ||
                Character.class == c;
    }

    private static void compareLists(Delta delta, Collection<Delta> deltas) {
        List srcList = (List) delta.srcValue;
        List targetList = (List) delta.targetValue;
        int srcLen = srcList.size();
        int targetLen = targetList.size();
        getRemovedDelta(srcLen, targetLen, srcList, targetList, delta, deltas);
        getAddedDelta(srcLen, targetLen, srcList, targetList, delta, deltas);
    }

    private static void getRemovedDelta(int srcLen, int targetLen, List srcList, List targetList, Delta delta, Collection<Delta> deltas) {
        LinkedList<Object> sourceStack = new LinkedList<>();
        for (int i = 0; i < srcLen; i++) {
            sourceStack.push(srcList.get(i));
        }
        while (!sourceStack.isEmpty()) {
            boolean isSrcPreset = false;
            Object srcData = sourceStack.pop();
            for (int i = 0; i < targetLen; i++) {
                if (CompareToBuilder.reflectionCompare(srcData, targetList.get(i)) == 0) {
                    isSrcPreset = true;
                    break;
                }
            }
            if (!isSrcPreset) {
                Delta t = new Delta(delta.fieldName, srcData, "", Operation.remove, delta.parent);
                deltas.add(t);
            }
        }
    }

    private static void getAddedDelta(int srcLen, int targetLen, List srcList, List targetList, Delta delta, Collection<Delta> deltas) {
        LinkedList<Object> targetStack = new LinkedList<>();
        for (int i = 0; i < targetLen; i++) {
            targetStack.push(targetList.get(i));
        }
        while (!targetStack.isEmpty()) {
            boolean isTargetPreset = false;
            Object targetData = targetStack.pop();
            for (int i = 0; i < srcLen; i++) {
                if (CompareToBuilder.reflectionCompare(targetData, srcList.get(i)) == 0) {
                    isTargetPreset = true;
                    break;
                }
            }
            if (!isTargetPreset) {
                Delta t = new Delta(delta.fieldName, "", targetData, Operation.add, delta.parent);
                deltas.add(t);
            }
        }
    }
}