package pattern.interpreter.pattern24_syntaxdirected

import pattern.interpreter.pattern24_syntaxdirected.q.QInterpreter

object SyntaxDirectedInterpreterApp extends App {
  val string =
    """create table users (primary key name, passwd, quota);
      |insert into users set name = 'parrt', passwd = 'foobar', quota = 99;
      |insert into users set name = 'tombu', passwd = 'spork',  quota = 200;
      |insert into users set name = 'sri', quota = 200;
      |
      |tombuQuota = select quota from users where name = 'tombu';
      |print tombuQuota;
      |
      |names = select passwd, name from users where quota = tombuQuota;
      |print names;
    """.stripMargin

  val interpreter = new QInterpreter(string)
  interpreter.run()
  /********** 出力 **********
   ResultSet(
     Result(200)
   )
   ResultSet(
     Result(null, sri)
     Result(spork, tombu)
   )
   *************************/
}