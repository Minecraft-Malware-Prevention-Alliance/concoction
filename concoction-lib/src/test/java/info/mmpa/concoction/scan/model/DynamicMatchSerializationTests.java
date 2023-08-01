package info.mmpa.concoction.scan.model;

import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.SusLevel;
import info.mmpa.concoction.scan.model.dynamic.DynamicMatchingModel;
import info.mmpa.concoction.scan.model.dynamic.entry.Condition;
import info.mmpa.concoction.scan.model.dynamic.entry.DynamicMatchEntry;
import info.mmpa.concoction.scan.model.insn.*;
import info.mmpa.concoction.scan.model.insn.entry.*;
import org.junit.jupiter.api.Test;
import software.coley.collections.Maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static info.mmpa.concoction.scan.model.TextMatchMode.EQUALS;
import static info.mmpa.concoction.util.TestSerialization.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for {@link DynamicMatchingModel}, {@link DynamicMatchEntry} and {@link Condition} serialization.
 */
public class DynamicMatchSerializationTests {

}
