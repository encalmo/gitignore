package org.encalmo.utils

import munit.ScalaCheckSuite

import org.scalacheck.Prop.*
import org.scalacheck.Gen

import org.encalmo.utils.Glob.LiteralPattern
import org.encalmo.utils.Glob.AnySingleCharacterPattern
import org.encalmo.utils.Glob.AnyStringPattern
import org.encalmo.utils.Glob.AnythingPattern
import org.encalmo.utils.Glob.BracketPattern
import org.encalmo.utils.Glob.CharacterClassCheck
import org.encalmo.utils.Glob.CharacterRangeCheck
import org.encalmo.utils.Glob.CompositeCharacterCheck
import org.encalmo.utils.Glob.NegatedCompositeCharacterCheck
import org.encalmo.utils.Glob.CompositePattern

class GlobCheckSpec extends ScalaCheckSuite {

  Debug.isDebug = false

  override def scalaCheckTestParameters =
    super.scalaCheckTestParameters
      .withMinSuccessfulTests(1000)

  def verify(pattern: String) = {
    val glob = Glob.compile(pattern)
    val testStringGenerator =
      Gen
        .sequence[List[String], String](
          Gen.asciiPrintableStr :: GlobTestStringGenerator(
            glob,
            GlobTestStringGenerator.asciiPrintableCharWithoutSlash
          ) :: Gen.asciiPrintableStr :: Nil
        )
        .map(_.mkString)
    property(s"finds $pattern") {
      forAll(testStringGenerator) { (s: String) =>
        val r = glob.matcher(s).find()
        assert(r)
      }
    }
  }

  Seq(
    "a",
    "a?",
    "?a",
    "a?a",
    "a?b",
    "a??b",
    "a*b",
    "a?*?b",
    "a**b",
    "abc123",
    "ac1b?",
    "?a2bc",
    "ab2c?cb1a",
    "aa7bc?cbb",
    "abc??cb3a",
    "ab6c*ccb",
    "aaa?*?bc8a",
    "ab0a**ab12a",
    "a?b?c?d?e?f?g?h",
    "?a?b?c?d?e?f?g?h?",
    "[a]",
    "[abc]",
    "[A-Z]",
    "[a-fG-Z]",
    "[!a]",
    "[!f-p]",
    "[A-Z]???[0-9]",
    "[ghj]*[0-9]",
    "/*",
    "/*/*",
    "/*/*/?",
    "/*/?/?",
    "/*/?/*",
    "/*/",
    "/*/*/",
    "/*/*/?/",
    "/*/?/?/",
    "/*/?/*/",
    "*/",
    "*/*/",
    "*/*/?/",
    "*/?/?/",
    "*/?/*/",
    "*/*",
    "*/*/*",
    "*/*/?/*",
    "*/?/?/*",
    "*/?/*/*"
  )
    .foreach(verify)

}

object GlobTestStringGenerator {

  val asciiPrintableCharWithoutSlash: Gen[Char] =
    Gen.asciiPrintableChar.filter(_ != '/')

  def apply(pattern: String, validCharacter: Gen[Char]): Gen[String] =
    apply(Glob.compile(pattern), validCharacter)

  def apply(pattern: Glob.Pattern, validCharacter: Gen[Char]): Gen[String] =
    pattern match {
      case CompositePattern(patterns) =>
        Gen.sequence[List[String], String](patterns.map(apply(_, validCharacter))).map(_.mkString)
      case LiteralPattern(literal)   => Gen.const(literal)
      case AnySingleCharacterPattern => validCharacter.map(_.toString())
      case AnyStringPattern          => Gen.stringOf(validCharacter)
      case AnythingPattern           => Gen.asciiPrintableStr
      case p: BracketPattern         => apply(p.characterCheck, validCharacter)
    }

  def apply(check: Glob.CharacterCheck, validCharacter: Gen[Char]): Gen[String] =
    check match {
      case CharacterClassCheck(characters) => Gen.oneOf(characters).map(_.toString())
      case CharacterRangeCheck(from, to)   => Gen.choose(from, to).map(_.toString())
      case CompositeCharacterCheck(checks) =>
        Gen.sequence[List[String], String](checks.map(apply(_, validCharacter))).flatMap(Gen.oneOf(_))
      case c: NegatedCompositeCharacterCheck => validCharacter.filter(c.check).map(_.toString())
    }

}
