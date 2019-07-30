package com.zzm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Unit test for simple App.
 */
public class ListTest {

    /**
     * @Description: guava测试用例
     */
    @Test
    public void givenList_whenParitioningIntoNSublists_thenCorrect() {
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> subSets = Lists.partition(intList, 3);

        List<Integer> lastPartition = subSets.get(2);
        List<Integer> expectedLastPartition = Lists.newArrayList(7, 8);
        assertThat(subSets.size(), equalTo(3));
        assertThat(lastPartition, equalTo(expectedLastPartition));
    }


    /**
     * @Description: guava collection
     */
    @Test
    public void givenCollection_whenParitioningIntoNSublists_thenCorrect() {
        Collection<Integer> intCollection = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

        Iterable<List<Integer>> subSets = Iterables.partition(intCollection, 3);
        intCollection.add(9);
        while (subSets.iterator().hasNext()) {
            List<Integer> firstPartition = subSets.iterator().next();
        }


        List<Integer> expectedLastPartition = Lists.newArrayList(1, 2, 3);
        // assertThat(firstPartition, equalTo(expectedLastPartition));
    }


    @Test
    public void givenListPartitioned_whenOriginalListIsModified_thenPartitionsChangeAsWell() {
        // Given
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> subSets = Lists.partition(intList, 3);

        // When
        intList.add(9);

        // Then
        List<Integer> lastPartition = subSets.get(2);
        List<Integer> expectedLastPartition = Lists.newArrayList(7, 8, 9);
        assertThat(lastPartition, equalTo(expectedLastPartition));
    }


    /**
    * @Description: apache common
    */
    @Test
    public void givenList_whenParitioningIntoNSublists_thenCorrectCommon() {
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> subSets = ListUtils.partition(intList, 3);
        List<Integer> lastPartition = subSets.get(2);
        List<Integer> expectedLastPartition = Lists.newArrayList(7, 8);
        assertThat(subSets.size(), equalTo(3));
        assertThat(lastPartition, equalTo(expectedLastPartition));

    }


    /**
    * @Description: java8
    */
    @Test
    public void givenList_whenParitioningIntoSublistsUsingPartitionBy_thenCorrect() {
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

        Map<Boolean, List<Integer>> groups =
                intList.stream().collect(Collectors.partitioningBy(s -> s > 6));
        List<List<Integer>> subSets = new ArrayList (groups.values());

        List<Integer> lastPartition = subSets.get(1);
        List<Integer> expectedLastPartition = Lists.newArrayList(7, 8);
        assertThat(subSets.size(), equalTo(2));
        assertThat(lastPartition, equalTo(expectedLastPartition));
    }


    /**
    * @Description: java 8 collectors#groupingby
    */
    @Test
    public final void givenList_whenParitioningIntoNSublistsUsingGroupingBy_thenCorrect() {
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

        Map<Integer, List<Integer>> groups =
                intList.stream().collect(Collectors.groupingBy(s -> (s - 1) / 3));
        List<List<Integer>> subSets = new ArrayList(groups.values());

        List<Integer> lastPartition = subSets.get(2);
        List<Integer> expectedLastPartition = Lists.newArrayList(7, 8);
        assertThat(subSets.size(), equalTo(3));
        assertThat(lastPartition, equalTo(expectedLastPartition));
    }

    /**
    * @Description: java8 分区
    */
    @Test
    public void givenList_whenSplittingBySeparator_thenCorrect() {
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 0, 4, 5, 6, 0, 7, 8);

        int[] indexes =
                Stream.of(IntStream.of(-1), IntStream.range(0, intList.size())
                        .filter(i -> intList.get(i) == 0), IntStream.of(intList.size()))
                        .flatMapToInt(s -> s).toArray();
        List<List<Integer>> subSets =
                IntStream.range(0, indexes.length - 1)
                        .mapToObj(i -> intList.subList(indexes[i] + 1, indexes[i + 1]))
                        .collect(Collectors.toList());

        List<Integer> lastPartition = subSets.get(2);
        List<Integer> expectedLastPartition = Lists. newArrayList(7, 8);
        assertThat(subSets.size(), equalTo(3));
        assertThat(lastPartition, equalTo(expectedLastPartition));
    }
}
