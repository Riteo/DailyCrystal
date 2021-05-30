package it.riteo.dailycrystal.utilities;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import com.comphenix.protocol.utility.MinecraftReflection;

/**
 * An utility class capable of managing the server's entity tracker.
 */
public class EntityUtilities {
	/* These are static utilities, we don't want to be able to instance them. */
	private EntityUtilities() {
	}

	private static AtomicInteger entityCount;

	/**
	 * Fetches a new entity ID through reflection, incrementing the server's
	 * counter.
	 *
	 * @throws ClassNotFoundException   if the <code>Entity</code> class doesn't
	 *                                  exist.
	 * @throws NoSuchFieldException     if the entity counter doesn't exist.
	 * @throws SecurityException        if the entity counter isn't accessible.
	 * @throws IllegalArgumentException if the entity counter isn't static
	 */
	public static int fetchNewEntityId() throws ClassNotFoundException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		if (entityCount == null) {
			Class<?> entityClass = MinecraftReflection.getEntityClass();
			Field entityCountField = entityClass.getDeclaredField("entityCount");
			entityCountField.setAccessible(true);

			/* We put null as the argument since the Entity class is static */
			entityCount = (AtomicInteger) entityCountField.get(null);
		}

		return entityCount.incrementAndGet();
	}
}
