# java中list分区

## 1.概述

本章中介绍如何分开几个子list在list中，对于简单操作，java标准collection api不支持，庆幸是

[Guava](https://github.com/google/guava) 和[Apache Commons Collections](http://commons.apache.org/proper/commons-collections/) 都实现操作

## 2.使用Guava分开list

Guava从list中分开指定大小sublist，使用**Lists.partition操作**     

```java
@Test
    public void givenList_whenParitioningIntoNSublists_thenCorrect() {
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
				//list被分成3个list subSets[0]->1，2，3  subSets[1]->4,5,6 subSets[2]->7,8
      	List<List<Integer>> subSets = Lists.partition(intList, 3);

        List<Integer> lastPartition = subSets.get(2);
        List<Integer> expectedLastPartition = Lists.<Integer> newArrayList(7, 8);
        assertThat(subSets.size(), equalTo(3));
        assertThat(lastPartition, equalTo(expectedLastPartition));
    }
```

## 3.使用Guava分区集合

集合分开也可以使用Guava

```java
  @Test
    public void givenCollection_whenParitioningIntoNSublists_thenCorrect() {
        Collection<Integer> intCollection = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);

        Iterable<List<Integer>> subSets = Iterables.partition(intCollection, 3);

        List<Integer> firstPartition = subSets.iterator().next();
        List<Integer> expectedLastPartition = Lists.newArrayList(1, 2, 3);
        assertThat(firstPartition, equalTo(expectedLastPartition));
    }
```

记住这个分区是原集合子视图，意味着更改原集合子视图也会改变

```java
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
```

## 4.使用**Apache Commons Collections** 分区list

最新版本[*Apache Commons Collections* ](https://github.com/apache/commons-collections) 也支持list分区

```java
 @Test
    public void givenList_whenParitioningIntoNSublists_thenCorrectCommon() {
        List<Integer> intList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> subSets = ListUtils.partition(intList, 3);
        List<Integer> lastPartition = subSets.get(2);
        List<Integer> expectedLastPartition = Lists.newArrayList(7, 8);
        assertThat(subSets.size(), equalTo(3));
        assertThat(lastPartition, equalTo(expectedLastPartition));

    }
```

最后也有相同tips，分区结果集合是原集合子视图

## 5.使用java8分区list

现在看下如何使用java8分区list

###5.1 **Collectors partitioningBy** 

使用 *Collectors.partitioningBy()*  分开list变成2个子list

```java
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
```

注意:分区结果不是原list子视图，因此任何原list更改不会影响分区视图

### 5.2 **Collectors groupingBy**

可以使用*Collectors.groupingBy()* 分开多个分区

```java
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
```

Tips:Collectors.groupingBy()分区结果不会被原list影响

### 5.3 分开list使用split

```java
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
```

tips：我们使用0作为分区点，第一获取索引为零元素，然后分开这些索引 list

## 6.总结

上面解决方案是引入第三方包（Guava or the Apache Commons Collections ），两种非常轻量级和很好用

项目中在classpath中引入其中一个包

[ **原文地址**](https://www.baeldung.com/java-list-split) 

