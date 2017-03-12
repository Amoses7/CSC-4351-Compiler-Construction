package Semant;
import java.util.Hashtable;
import Translate.Exp;
import Types.Type;
import Absyn.ExpList;
import Absyn.SeqExp;

public class Semant {
  Env env;
  public Semant(ErrorMsg.ErrorMsg err) {
    this(new Env(err));
  }
  Semant(Env e) {
    env = e;
  }

  public void transProg(Absyn.Exp exp) {
    transExp(exp);
  }
  
  private void transArgs(int epos, Types.RECORD formal, Absyn.ExpList args)
  {
    if (form == null)
    {
      if (args != null)
      {
        error(args.head.pos, "");}
    }
    if (args == null){
      error(epos, "missing argument" + formal.fieldName);
    }
    if (!e.ty.coerceTo(formal.fieldType))
      error(args.head.pos, "argument mismatch");
  }

  private void error(int pos, String msg) {
    env.errorMsg.error(pos, msg);
  }

  static final Types.VOID   VOID   = new Types.VOID();
  static final Types.INT    INT    = new Types.INT();
  static final Types.STRING STRING = new Types.STRING();
  static final Types.NIL    NIL    = new Types.NIL();

  private Exp checkInt(ExpTy et, int pos) {
    if (!INT.coerceTo(et.ty))
      error(pos, "integer required");
    return et.exp;
  }
  
    private Exp checkComparable(ExpTy et, int pos){
	  Type a = et.ty.actual();
	  if (!(a instanceof Types.INT
			  || a instanceof Types.STRING
			  || a instanceof Types.NIL
			  || a instanceof Types.RECORD
			  || a instanceof Types.ARRAY))
		  error(pos, "Int, String, Nil, Record or Array required");
	  return et.exp;
  }
  
  private Exp checkOrderable(ExpTy et, int pos){
	  Type a = et.ty.actual();
	  if (!(a instanceof Types.INT
			  || a instanceof Types.STRING))
		  error(pos, "Int or String required");
	  return et.exp;
  }
  //DONE
  private void putTypeFields(Types.RECORD f){
	  if (f == null)
		  return;
	  env.venv.put(f.fieldName, new VarEntry(f.fieldType));
	  putTypeFields(f.tail);
  }

  ExpTy transExp(Absyn.Exp e) {
    ExpTy result;

    if (e == null)
      return new ExpTy(null, VOID);
    else if (e instanceof Absyn.OpExp)
      result = transExp((Absyn.OpExp)e);
    else if (e instanceof Absyn.LetExp)
      result = transExp((Absyn.LetExp)e);
    else if (e instanceof Absyn.VarExp)
	    result = transExp((Absyn.VarExp)e);
    else if (e instanceof Absyn.NilExp)
	    result = transExp((Absyn.NilExp)e);
    else if (e instanceof Absyn.IntExp)
	    result = transExp((Absyn.IntExp)e);
    else if (e instanceof Absyn.StringExp)
	    result = transExp((Absyn.StringExp)e);
    else if (e instanceof Absyn.CallExp)
	    result = transExp((Absyn.CallExp)e);
    else if (e instanceof Absyn.RecordExp)
 	    result = transExp((Absyn.RecordExp)e);
    else if (e instanceof Absyn.SeqExp)
    	result = transExp((Absyn.SeqExp)e);
    else if (e instanceof Absyn.AssignExp)
	    result = transExp((Absyn.AssignExp)e);
    else if (e instanceof Absyn.IfExp)
	    result = transExp((Absyn.IfExp)e);
    else if (e instanceof Absyn.WhileExp)
	    result = transExp((Absyn.WhileExp)e);
    else if (e instanceof Absyn.ForExp)
	    result = transExp((Absyn.ForExp)e);
    else if (e instanceof Absyn.BreakExp)
	    result = transExp((Absyn.BreakExp)e);
    else if (e instanceof Absyn.ArrayExp)
	    result = transExp((Absyn.ArrayExp)e);
    else throw new Error("Semant.transExp");
    e.type = result.ty;
    return result;
  }

  ExpTy transExp(Absyn.OpExp e) {
    ExpTy left = transExp(e.left);
    ExpTy right = transExp(e.right);

    switch (e.oper) {
    case Absyn.OpExp.PLUS:
      checkInt(left, e.left.pos);
      checkInt(right, e.right.pos);
      return new ExpTy(null, INT);
     case Absyn.OpExp.MINUS:
      checkInt(left, e.left.pos);
      checkInt(right, e.right.pos);
      return new ExpTy(null, INT);
    case Absyn.OpExp.MUL:
      checkInt(left, e.left.pos);
      checkInt(right, e.right.pos);
      return new ExpTy(null, INT);
    case Absyn.OpExp.DIV:
      checkInt(left, e.left.pos);
      checkInt(right, e.right.pos);
      return new ExpTy(null, INT);
    case Absyn.OpExp.EQ:
      checkComparable(left, e.left.pos);
      checkComparable(right, e.right.pos);
      if(STRING.coerceTo(left.ty) && STRING.coerceTo(right.ty))
    	  return new ExpTy(null, INT);
      else if (!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty))
    	  error(e.pos, "Operands not valid");
      return new ExpTy(null, INT);
    case Absyn.OpExp.NE:
    	checkComparable(left, e.left.pos);
        checkComparable(right, e.right.pos);
        if(STRING.coerceTo(left.ty) && STRING.coerceTo(right.ty))
          return new ExpTy(null, INT);
      else if (!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty))
        error(e.pos, "Operands not valid for equality");
      return new ExpTy(null, INT);
    case Absyn.OpExp.LT:
      checkOrderable(left, e.left.pos);
      checkOrderable(right, e.right.pos);
      if(STRING.coerceTo(left.ty) && STRING.coerceTo(right.ty))
    	  return new ExpTy(null, INT);
      else if(!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty))
    	  error(e.pos, "Operands not valid for inequality");
      return new ExpTy(null, INT);
    case Absyn.OpExp.LE:
    	checkOrderable(left, e.left.pos);
        checkOrderable(right, e.right.pos);
        if(STRING.coerceTo(left.ty) && STRING.coerceTo(right.ty))
      	  return new ExpTy(null, INT);
        else if(!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty))
      	  error(e.pos, "Operands not valid for inequality");
        return new ExpTy(null, INT);
    case Absyn.OpExp.GT:
    	checkOrderable(left, e.left.pos);
        checkOrderable(right, e.right.pos);
        if(STRING.coerceTo(left.ty) && STRING.coerceTo(right.ty))
      	  return new ExpTy(null, INT);
        else if(!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty))
      	  error(e.pos, "Operands not valid for inequality");
        return new ExpTy(null, INT);
    case Absyn.OpExp.GE:
    	checkOrderable(left, e.left.pos);
        checkOrderable(right, e.right.pos);
        if(STRING.coerceTo(left.ty) && STRING.coerceTo(right.ty))
      	  return new ExpTy(null, INT);
        else if(!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty))
      	  error(e.pos, "Operands not valid for inequality");
        return new ExpTy(null, INT); 
    default:
      throw new Error("unknown operator");
    }
  }

  ExpTy transExp(Absyn.LetExp e) {
    env.venv.beginScope();
    env.tenv.beginScope();
    for (Absyn.DecList d = e.decs; d != null; d = d.tail) {
      transDec(d.head);
    }
    ExpTy body = transExp(e.body);
    env.venv.endScope();
    env.tenv.endScope();
    return new ExpTy(null, body.ty);
  }
  ExpTy transExp(Absyn.VarExp e) {
	return transVar(e.var);
  } 
  ExpTy transExp(Absyn.NilExp e) {
	return new ExpTy(null, NIL);
  }
  ExpTy transExp(Absyn.IntExp e) {
        return new ExpTy(null, INT);
  }
  ExpTy transExp(Absyn.StringExp e) {
	return new ExpTy(null, STRING);
  }
  ExpTy transExp(Absyn.CallExp e) {
	  Entry x = (Entry)env.venv.get(e.func);
	  if (x instanceof FunEntry) {
		  FunEntry f = (FunEntry)x;
		  transArgs(e.pos, f.formals, e.args);
		  return new ExpTy(null, f.result);
	  }
	  error(e.pos, "function undeclared" + e.func);
	  return new ExpTy(null, VOID);
  }	
  
  ExpTy transExp(Absyn.RecordExp e) {
   Types.NAME name = (Types.NAME)env.tenv.get(e.typ);
   if(name != null) {
	   Type actual = name.actual();
	   if( actual instanceof Types.RECORD){
		   Types.RECORD r = (Types.RECORD)actual;
		   transFields(e.pos, r, e.fields);
		   return new ExpTy(null, name);
	   }
	   error(e.pos, "Record type required");
   }else
	   error(e.pos, "undeclared type" + e.typ);
   return new ExpTy(null, VOID);
  }
  ExpTy transExp(Absyn.SeqExp e) {
	  Type type = VOID;
	  ExpList head = new ExpList(null, null), prev = head;
	  for (Absyn.ExpList exp = e.list; exp != null; exp = exp.tail){
		  ExpTy et = transExp(exp.head);
		  type = et.ty;
		  //prev = prev.tail = new ExpList(et.exp, head.tail);
	  }
	  return new ExpTy(null, type);
  }
  ExpTy transExp(Absyn.AssignExp e) {
	ExpTy var = transVar(e.var);
	ExpTy exp = transExp(e.exp);
	if (exp.ty.coerceTo(var.ty))
	   error(e.pos, "Mismatch type: Assignement");
	return new ExpTy(null, VOID);
  }
  ExpTy transExp(Absyn.IfExp e) {
	ExpTy test = transExp(e.test);
	checkInt(test, e.test.pos);
	ExpTy thenclause = transExp(e.thenclause);
	ExpTy elseclause = transExp(e.elseclause);
	if(!thenclause.ty.coerceTo(elseclause.ty) && !elseclause.ty.coerceTo(thenclause.ty))
		error (e.pos, "result type mismatch");
	return new ExpTy(null, elseclause.ty);
  }
  ExpTy transExp(Absyn.WhileExp e){
	 ExpTy tt = transExp(e.test);
	 checkInt(tt, e.test.pos);
	 LoopSemant loop = new LoopSemant(env);
	 ExpTy body = loop.transExp(e.body);
	 if(!body.ty.coerceTo(VOID))
		 error(e.body.pos, "result type mismatch");
	 return new ExpTy(null, VOID);
  }
  ExpTy transExp(Absyn.ForExp e){
	   ExpTy lo = transExp(e.var.init);
	   checkInt(lo, e.var.pos);
	   ExpTy hi = transExp(e.hi);
	   checkInt(hi, e.hi.pos);
	   e.var.entry = new LoopVarEntry(INT);
	   env.venv.put(e.var.name, e.var.entry);
	   Semant loop = new LoopSemant(env);
	   ExpTy body = loop.transExp(e.body);
	   env.venv.endScope();
	   if (!body.ty.coerceTo(VOID))
		   error(e.body.pos, "result type mismatch");
	   return new ExpTy(null, VOID);
  }
  ExpTy transExp(Absyn.BreakExp e){
	  error(e.pos, "break outside loop");
	  return new ExpTy(null, VOID);
  }
  
  ExpTy transExp(Absyn.ArrayExp e){
	  Types.NAME name = (Types.NAME)env.tenv.get(e.typ);
	  ExpTy size = transExp(e.size);
	  ExpTy init = transExp(e.init);
	  checkInt(size, e.size.pos);
	  if (name != null) {
		  Type actual = name.actual();
		  if( actual instanceof Types.ARRAY){
			  Types.ARRAY array = (Types.ARRAY)actual;
			  if(!init.ty.coerceTo(array.element))
				  error(e.init.pos, "element type mismatch");
			  return new ExpTy(null, name);
			  
		  } else
			  error(e.pos, "array type required");
	  }	else
		  error(e.pos, "type not declared");
	  return new ExpTy(null,  VOID);
  }
  
  
  Exp transDec(Absyn.Dec d) {
    if (d instanceof Absyn.VarDec)
      return transDec((Absyn.VarDec)d);
    if (d instanceof Absyn.FunctionDec)
      return transDec((Absyn.FunctionDec)d);
    if (d instanceof Absyn.TypeDec)
      return transDec((Absyn.TypeDeck)d);
    throw new Error("Semant.transDec");
  }

  Exp transDec(Absyn.VarDec d) {
    ExpTy init = transExp(d.init);
    Type type;
    if (d.typ == null) {
      type = init.ty;
    } else {
      type = VOID;
      throw new Error("unimplemented");
    }
    d.entry = new VarEntry(type);
    env.venv.put(d.name, d.entry);
    return null;
  }
	
	Exp transDeck(Absyn.FunctionDec d)
	{
		Hashtable hash = new Hashtable();
		for (Absyn.FunctionDec f = d; f != null; f = f.next)
		{
			if (hash.put(f.name,f.name) != null)
				error(f.pos, "function redecl");
			Types.RECORD fields = transTypeFields(new Hashtable(), f.params);
			Type type = transTy(f.result);
			f.entry = new FunEntry(fields, type);
			env.venv.put(f.name, f.entry);
		}
		
		 for (Absyn.FunctionDec f = d; f != null; f = f.next) {
		  env.venv.beginScope();
		  putTypeFields(f.entry.formals);
		  Semant fun = new Semant(env);
		  ExpTy body = fun.transExp(f.body);
		  if(!body.ty.coerceTo(f.entry.result))
			  error(f.body.pos, "result type incompatible");
		  env.venv.endScope();
	  }
	  return null;
		
		Type transTy(Absyn.Ty t){
	  if (t instanceof Absyn.NameTy)
		  return transTy((Absyn.NameTy)t);
	  if (t instanceof Absyn.RecordTy)
		  return transTy((Absyn.RecordTy)t);
	  if (t instanceof Absyn.ArrayTy)
		  return transTy((Absyn.ArrayTy)t);
	  throw new Error("TransTy Semant");
  }
  Type transTy(Absyn.NameTy t){
	  if (t == null)
		  return VOID;
	  Types.NAME name = (Types.NAME)env.tenv.get(t.name);
	  if (name != null)
		  return name;
	  error(t.pos, "undeclared type: " + t.name);
	  return VOID;
  }
  Type transTy(Absyn.RecordTy t){
	  Types.RECORD type = transTypeFields(new Hashtable(), t.fields);
	  if (type != null)
		  return type;
	  return VOID;
  }
  Type transTy(Absyn.ArrayTy t){
	  Types.NAME name = (Types.NAME)env.tenv.get(t.typ);
	  if (name != null)
		  return new Types.ARRAY(name);
	  error(t.pos, "undeclared type: " + t.typ);
	  return VOID;
  }
  ExpTy transVar(Absyn.SimpleVar e) {
    Entry x = (Entry)env.venv.get(e.name);
	if (x instanceof VarEntry) {
	    VarEntry ent = (VarEntry)x;
	    return new ExpTy(null, ent.ty);
	}
	else {
	   error(e.pos, "undefined variable");
	   return new ExpTy(null, INT);
	}
  
    }
  ExpTy transVar(Absyn.Var v){
	  if (v instanceof Absyn.SimpleVar)
		  return transVar((Absyn.SimpleVar)v);
	  if (v instanceof Absyn.FieldVar)
		  return transVar((Absyn.FieldVar)v);
	  if (v instanceof Absyn.SubscriptVar)
		  return transVar((Absyn.SubscriptVar)v);
	  throw new Error("TransVar Semant");
  }
  ExpTy transVar(Absyn.FieldVar v){
	  ExpTy var = transVar(v.var);
	  Type actual = var.ty.actual();
	  if (actual instanceof Types.RECORD){
		  int count = 0;
		  for(Types.RECORD field = (Types.RECORD)actual; field != null; field = field.tail){
			  if (field.fieldName == v.field)
				  return new ExpTy(null, field.fieldType);
		  ++count;
		  }
		  error(v.pos, "undeclared field: " + v.field);
	  } else
		  error(v.var.pos, "record required");
	  return new ExpTy(null, VOID);
  }
  
  ExpTy transVar(Absyn.SubscriptVar v){
	  ExpTy var = transVar(v.var);
	  ExpTy index = transExp(v.index);
	  checkInt(index, v.index.pos);
	  Type actual = var.ty.actual();
	  if (actual instanceof Types.ARRAY) {
		  Types.ARRAY array = (Types.ARRAY)actual;
		  return new ExpTy(null, array.element);
	  }
	  error(v.var.pos, "array required");
	  return new ExpTy(null, VOID);
  }
  private Types.RECORD transTypeFields(Hashtable hash, Absyn.FieldList f){
	  if (f == null)
		  return null;
	  Types.NAME name = (Types.NAME)env.tenv.get(f.typ);
	  if (name == null)
		  error(f.pos, "undeclared type: " + f.typ);
	  if( hash.put(f.name, f.name) != null)
		  error(f.pos, "function redeclared" + f.name);
	  return new Types.RECORD(f.name, name, transTypeFields(hash, f.tail));
  }
  private void transFields(int epos, Types.RECORD f, Absyn.FieldExpList exp)
  {
	    if (f == null) {
	      if (exp != null)
	        error(exp.pos, "too many expressions");
	      //return null;
	    }
	    if (exp == null) {
	      error(epos, "missing expression for " + f.fieldName);
	      //return null;
	    }
	    ExpTy e = transExp(exp.init);
	    if (exp.name != f.fieldName)
	      error(exp.pos, "field name mismatch");
	    if (!e.ty.coerceTo(f.fieldType))
	      error(exp.pos, "field type mismatch");
   }
  
}
class LoopSemant extends Semant{
	  LoopSemant(Env e){
		  super(e);
		//  done = new temp.Label();
	  }
}

