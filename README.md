# Nest4J

[en_us version](./README_en_us.md)

Nest4J是一款基于Java作为开发语言的Nest算法包。可以看做一款能在服务端进行运行计算的Nest算法库。 基于[SVGNest](https://github.com/Jack000/SVGnest)进行了Java化的改造。

同样这也作为了我本科的毕业设计，让我领略到了计算几何和遗传算法的魅力。

## 什么是套料？

在给定一个矩形底板和以及一些字母材料时，如何将这些字母材料尽可能的塞进这个矩形底板并且保持字母两两之间并不会重合？通过一些特殊的摆放顺序与位置以及将每个字母旋转到合适的角度，我们可以达到这个目的。而如何去计算出材料与地板之间的位置关系以及材料的旋转角度，或者其他更高的要求，我们将这类问题称之为套料问题。


![example](./png/nest.png)

更多细节请参考[SVGNest](https://github.com/Jack000/SVGnest)


## 效果

我使用了SVGNest的Demo例子让Nest4J进行计算。得出了以下的效果图

![sample](./png/sample.png)


## 如何使用

本算法基于SVGNest,做了Java化的改造，使得这个算法可以运行在服务器环境中进行后台计算服务。

Nest4J的使用方法非常简单。通过以下几步，你就可以轻松掌握如何使用Nest4J

### 环境

Nest4J使用JDK1.8版本编写而成，使用到了[Clipper-java](https://github.com/lightbringer/clipper-java)的maven依赖。
所以你需要先将[Clipper-java](https://github.com/lightbringer/clipper-java)下载至本地后打成本地依赖才能将Nest4J打成mvn依赖包。


### 多边形表示方法

Nest4J的多边形表示方法就是通用的通过点集来表示一个多边形。下面这个例子向我们展示了如何构造出一个矩形的多边形。

**值得注意的是，Nest4J表示多边形的方法是基于一个二维直角坐标系，即你需要保证输入的多边形两两之间并不会重合，否则就会引起报错。**

```java
        NestPath bin = new NestPath();
        double binWidth = 511.822;
        double binHeight = 339.235;
        bin.add(0, 0);
        bin.add(binWidth, 0);
        bin.add(binWidth, binHeight);
        bin.add(0, binHeight);

```

### 构造板件素材

当我们知道如何构造多边形时，在构造板件集合时，其实就是构造一个多边形的集合。

```java
        List<NestPath> list = new ArrayList<NestPath>();
        list.add(inner);
        list.add(outer);
        list.add(little);

```

### 板件的扩展选择

当一个板件被构造出来时，他的默认Rotation属性为0，这意味着我们在套料过程中固定住这个板件，不能使他旋转。为了让他旋转，我们可以设置他的Rotation属性为4，这意味着这个板件在套料过程中可以旋转90°，180°，270°。即当我们设置某个板件的Rotation为N时，这个板件在套料过程中就有旋转**(360/N)\*k , k= 0,1,2,3,...N**这些选择。同时，你也可以设置每个板件的**bid**属性，这将帮助你在套料开始前与套料结束后对你的板件一一对应。

**与SVGNest不同，你可以在构造板件过程中自己设置每个板件的旋转角度，使得每个板件的旋转角度选择不同，建议rotation属性设置为360的因数**

``` java

      polygon.bid = id;
      polygon.setRotation(4);

``` 


### 空心板件

对于某些存在空心的板件，空心可以不止一个空心。Nest4J表示一个存在空心的多边形也非常简单,只要通过将这个空心的图形通过二维平面直角坐标系描述出来以后，在板件的内部中即可。Nest4J会自动发现板件中存在空心。

```java

        NestPath outer = new NestPath();
        outer.add(600, 0);
        outer.add(600, 200);
        outer.add(800, 200);
        outer.add(800, 0);
        outer.setRotation(0);
        outer.bid = 1;
        NestPath inner = new NestPath();
        inner.add(650, 50);
        inner.add(650, 150);
        inner.add(750, 150);
        inner.add(750, 50);
        inner.bid = 2;

```

### 属性设置

Nest4J在开始套料计算前，允许用户进行一些自定义的设置。


```java
        Config config = new Config();
        config.SPACING = 0;
        config.POPULATION_SIZE = 5;
```


<table>
    <tr>
        <td>属性</td>
        <td>描述</td>
        <td>默认值</td>
    </tr>
    <tr>
        <td>SPACING</td>
        <td>在套料过程中，所有板件两两之间的距离</td>
        <td>0</td>
    </tr>
    <tr>
        <td>POPULATION_SIZE</td>
        <td>利用遗传算法时所生成的族群个体数量</td>
        <td>10</td>
    </tr>
    <tr>
        <td>MUTATION_RATE</td>
        <td>利用遗传算法时，套料顺序的变异几率</td>
        <td>百分之10</td>
    </tr> 
    <tr>
        <td>USE_HOLE</td>
        <td>当板件中存在空心板件时，是否允许将板件放在空心板件当中</td>
        <td>false</td>
    </tr>     
</table>

### 开始计算

当我们配置好所需要的底板，板件集，以及相关参数后，以及我们需要的迭代次数，我们便可以开始进行套料计算。

```java

     Nest nest = new Nest(bin, polygons, config, 2);
     List<List<Placement>> appliedPlacement = nest.startNest();

```

### Placement

Placement作为我们最后计算结果的一个单元，代表着某个Bid的板件,相对于他的底板左上角的偏移值和旋转角度。 我们最终拿到了一个`List<List<Placement>> `的计算结果，其中`<List<Placement>`代表着在一块底板中，某些板件的偏移值及旋转角度。

```java
public class Placement {
    public int bid;
    public Segment translate;
    public double rotate;


    public Placement(int bid, Segment translate, double rotate) {
        this.bid = bid;
        this.translate = translate;
        this.rotate = rotate;
    }

    public Placement() {
    }
}

```

### 可视化

对于输出结果，我提供了一种基于SVG可视化的方法。你可以在`NestTest`中查看。

```java
        List<String> strings = SvgUtil.svgGenerator(polygons, appliedPlacement, binWidth, binHeight);
        saveSvgFile(strings);

```

### 参考论文

- [López-Camacho *et al.* 2013](http://www.cs.stir.ac.uk/~goc/papers/EffectiveHueristic2DAOR2013.pdf)
- [Kendall 2000](http://www.graham-kendall.com/papers/k2001.pdf)
- [E.K. Burke *et al.* 2006](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.440.379&rep=rep1&type=pdf)

## TODO

1. 基于Java1.8进行语言层面的优化
2. 进行多线程改造优化效率
3. 改造NFP的缓存策略。
4. 改造多边形的放大缩小计算效率。

