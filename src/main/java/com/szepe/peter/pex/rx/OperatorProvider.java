package com.szepe.peter.pex.rx;

import java.util.function.Supplier;

public interface OperatorProvider<T, R, E extends Exception> extends Supplier<Operator<T, R, E>> {
}
