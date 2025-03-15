package org.vstu.meaningtree.utils;

/**
 * Исключение, выбрасываемое когда метод или операция не реализованы
 */
public class NotImplementedException extends UnsupportedOperationException {
    private final String methodName;

    /**
     * Создает новый экземпляр NotImplementedException
     *
     * @param methodName Имя нереализованного метода
     */
    public NotImplementedException(String methodName) {
        super(String.format("%s not implemented", methodName));
        this.methodName = methodName;
    }

    /**
     * Возвращает имя нереализованного метода
     *
     * @return Имя метода
     */
    public String getMethodName() {
        return methodName;
    }
} 