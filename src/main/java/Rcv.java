import java.util.Optional;

public interface Rcv<R, I> {
    Optional<R> rcv(I input);
}
