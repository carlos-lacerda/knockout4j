knockout4j
==========

Java port of Knockout.js


Comparison
----------

<table><tr>
  <th>Knockout.js</th>
  <th>knockout4j</th>
</tr>

<tr><td><pre>
ko.observable('hello')
</pre></td><td><pre>
new KO.Observable&lt;String&gt;("Hello")
</pre></td></tr>

<tr><td><pre>
ko.computed(function () { return x() + y();  })
</pre></td><td><pre>
new KO.Computed&lt;Integer&gt;() {
  Integer evaluate() {
    return x.get() + y.get();
  }
}
</pre></td></tr>

</table>
