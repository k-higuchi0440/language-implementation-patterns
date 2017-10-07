package pattern.interpreter.pattern25_treebased

import pattern.interpreter.pattern25_treebased.pie.PieInterpreter

object TreeBasedInterpreterApp extends App {
  val string =
    """print "----- func forward ref -----"
      |print "f(4) = " + f(4) # forward ref
      |def f(x) return 2 * x
      |
      |print "----- while loop -----"
      |i = 0
      |maxCnt = 5
      |while i < maxCnt:
      |	 i = i + 1
      |  print i + " * " + 3.2 + " = " + i * 3.2
      |	 if i < 3 print "(" + i + " is less than 3" + ")"
      |	 else print "(" + i + " is more than 3" + ")"
      |.
      |
      |print "----- global and local struct -----"
      |struct User { name, password }
      |
      |def printLocalUser():
      |  user = new User # forward ref
      |  user.x = "local"
      |  print "(local) " + user
      |  struct User { x, y }
      |.
      |
      |print "(global) " + new User   # User{name, password}
      |printLocalUser() # User{x, y}
      |
      |print "----- recursion factorial -----"
      |def fact(n):
      |	if n < 2 return 1
      |	return n * fact(n-1)
      |.
      |print "fact(10) = " + fact(10)
    """.stripMargin

  val interpreter = new PieInterpreter(string)
  interpreter.run()
  /********** 出力 **********
   ----- func forward ref -----
   f(4) = 8
   ----- while loop -----
   1 * 3.2 = 3.2
   (1 is less than 3)
   2 * 3.2 = 6.4
   (2 is less than 3)
   3 * 3.2 = 9.6
   (3 is more than 3)
   4 * 3.2 = 12.8
   (4 is more than 3)
   5 * 3.2 = 16.0
   (5 is more than 3)
   ----- global and local struct -----
   (global) User{ name = None, password = None }
   (local) User{ y = None, x = local }
   ----- recursion factorial -----
   fact(10) = 3628800
   *************************/
}
