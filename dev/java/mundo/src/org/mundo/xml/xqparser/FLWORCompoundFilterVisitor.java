package org.mundo.xml.xqparser;

import java.util.HashMap;
import java.util.Map;

import org.mundo.filter.CompoundAndFilter;
import org.mundo.filter.CompoundFilter;
import org.mundo.filter.CompoundOrFilter;
import org.mundo.filter.IFilter;
import org.mundo.filter.IFilterConstants;

public class FLWORCompoundFilterVisitor implements XParserVisitor,
		XParserTreeConstants {

	public Map vars = new HashMap<String, Node>();

	@Override
	public Object visit(SimpleNode node, Object data) {
		switch (node.id()) {
		case JJTFLWOREXPR10:
			return flworExpr(node);
		default:
			for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
				node.jjtGetChild(i).jjtAccept(this, data);
			}
		}
		return data;
	}

	CompoundFilter flworExpr(SimpleNode node) {
		System.out.println("FLWORExpr #children: " + node.jjtGetNumChildren());
		for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
			SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			switch (child.id()) {
			case JJTFORCLAUSE:
				forClause(child);
				break;
			case JJTWHERECLAUSE:
				whereClause(child);
				break;
			case JJTLETCLAUSE:
				System.out.println("let clause unsupported for now.");
				break;
			case JJTORDERBYCLAUSE:
				System.out.println("let clause unsupported for now.");
				break;
			case JJTEXPR:
				System.out.println("return with flwor unsupported for now.");
				break;
			}
		}
		return null;
	}

	public CompoundFilter whereClause(SimpleNode node) {
		System.out.println("ForClause #children: " + node.jjtGetNumChildren());
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			switch (child.id()) {
			case JJTPARENTHESIZEDEXPR:
				parenthesizedExpr(child);
			case JJTVARNAME:
				break;
			default:
				throw new RuntimeException("Unsupported construct in where clause");
			}
		}

		return null;
	}

	public CompoundFilter parenthesizedExpr(SimpleNode node) {
		assert node.id() == JJTPARENTHESIZEDEXPR : "Expected a node of type PARENTHESIZEDEXPR";
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			switch (child.id()) {
			case JJTEXPR:
				return expr(child);
			default:
				throw new RuntimeException("Type not allowed within parenthesized expression");
			}
		}
		return null;
	}

	private CompoundFilter expr(SimpleNode node) {
		assert node.id() == JJTEXPR : "Expected a node of type EXPR";
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			switch (child.id()) {
			case JJTANDEXPR:
				return and(child);
			default:
				throw new RuntimeException("Type not allowed within parenthesized expression");
			}
		}
		return null;
	}

	private CompoundAndFilter and(SimpleNode node) {
		assert node.id() == JJTANDEXPR : "Expected a node of type ANDEXPR";
		assert node.jjtGetNumChildren() == 2 : "Expected two expressions within an ANDEXPR";
		CompoundAndFilter caf = new CompoundAndFilter();

		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			switch (child.id()) {
			case JJTCOMPARISONEXPR:
				caf.add(comparison(child));
			default:
				throw new RuntimeException("Type not allowed within parenthesized expression");
			}
		}
		return null;
	}

	private IFilter comparison(SimpleNode node) {
		assert node.id() == JJTCOMPARISONEXPR : "Expected a node of type COMPARISONEXPR";
		assert node.jjtGetNumChildren() == 2 : "Expected two expressions within an COMPARISONEXPR";
		int filterOp = IFilterConstants.OP_IGNORE;
		String filterSub = null;
		if ("=".equals(node.value)) {
			filterOp = IFilterConstants.OP_EQUAL;
		}
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			switch (child.id()) {
			case JJTDECIMALLITERAL:
				
				break;
			default:
				throw new RuntimeException("Type not allowed within parenthesized expression");
			}
		}
		return null;
	}

	// @TODO: allow nested for loops with vars
	public void forClause(SimpleNode node) {
		assert node.id() == JJTFORCLAUSE : "Expected a node of type FORCLAUSE";
		System.out.println("ForClause #children: " + node.jjtGetNumChildren());
		assert node.jjtGetNumChildren() == 2 : "Only simple 'for varname in expression' are supported for now.";
		// for each pair
		for (int i = 0; i < node.jjtGetNumChildren(); i += 2) {
			SimpleNode var = (SimpleNode) node.jjtGetChild(i);
			SimpleNode expr = (SimpleNode) node.jjtGetChild(i + 1);
			switch (expr.id()) {
			case JJTPATHEXPR:
				vars.put(varName(var), expr);
				System.out.println(vars);
				break;
			default:
				throw new RuntimeException("Only 'VarName in VarName|PathExpr' allowed with 'for' clauses.");
			}
		}
	}

	public String varName(SimpleNode node) {
		assert node.id() == JJTVARNAME : "Expected a node of type VARNAME";
		return qName((SimpleNode) node.jjtGetChild(0));
	}

	public String qName(SimpleNode node) {
		assert node.id() == JJTQNAME: "Expected a node of type QNAME";
		return functionQName((SimpleNode) node.jjtGetChild(0));
	}

	public String functionQName(SimpleNode node) {
		assert node.id() == JJTFUNCTIONQNAME: "Expected a node of type FUNCTIONQNAME";
		return node.value;
	}

}
