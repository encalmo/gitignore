<a href="https://github.com/encalmo/gitignore">![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)</a> <a href="https://central.sonatype.com/artifact/org.encalmo/gitignore_3" target="_blank">![Maven Central Version](https://img.shields.io/maven-central/v/org.encalmo/gitignore_3?style=for-the-badge)</a> <a href="https://encalmo.github.io/gitignore/scaladoc/org/encalmo/utils.html" target="_blank"><img alt="Scaladoc" src="https://img.shields.io/badge/docs-scaladoc-red?style=for-the-badge"></a>

# gitignore

The [.gitignore](https://git-scm.com/docs/gitignore) has became de-facto standard filter format for project's files and folders. 

This nano-library provides Scala implementation of the `.gitignore` compliant filter, ready to embed in different tools without having to run `git` command.

## Table of contents

- [Dependencies](#dependencies)
- [Usage](#usage)
- [Examples](#examples)

## Dependencies

   - [Scala](https://www.scala-lang.org) >= 3.3.5

## Usage

Use with SBT

    libraryDependencies += "org.encalmo" %% "gitignore" % "0.9.2"

or with SCALA-CLI

    //> using dep org.encalmo::gitignore:0.9.2

## Examples

Option **1** - parse existing `.gitignore` file content

```scala
val gitIgnore = GitIgnore.fromCurrentDirectory()
gitIgnore.isAllowed(".scala-build/") shouldBe false
gitIgnore.isAllowed("GitIgnore.scala") shouldBe true
```

```scala
val gitIgnore = GitIgnore.fromFile(new File(".gitignore"))
gitIgnore.isAllowed(".scala-build/") shouldBe false
gitIgnore.isAllowed("GitIgnore.scala") shouldBe true
```

```scala
import com.github.arturopala.gitignore._

val gitignore = GitIgnore
    .parse(""" 
        |#*.json
        |*.txt
        |*.pdf
        |!ok.*
        |bar.json 
        |target  
        |""".stripMargin)
// gitignore: GitIgnore = GitIgnore(
//   gitPatterns = List("*.txt", "*.pdf", "!ok.*", "bar.json", "target")
// )
 
gitignore.isIgnored("foo.txt")
// res0: Boolean = true
gitignore.isIgnored("bar.txt")
// res1: Boolean = true
gitignore.isIgnored("ok.txt")
// res2: Boolean = false
gitignore.isIgnored("foo.pdf")
// res3: Boolean = true
gitignore.isIgnored("bar.pdf")
// res4: Boolean = true
gitignore.isIgnored("ok.pdf")
// res5: Boolean = false
gitignore.isIgnored("foo.json")
// res6: Boolean = false
gitignore.isIgnored("bar.json")
// res7: Boolean = true
gitignore.isIgnored("ok.json")
// res8: Boolean = false
gitignore.isIgnored("target/")
// res9: Boolean = true
gitignore.isIgnored("target.json")
// res10: Boolean = false

gitignore.isAllowed("foo.txt")
// res11: Boolean = false
gitignore.isAllowed("bar.txt")
// res12: Boolean = false
gitignore.isAllowed("ok.txt")
// res13: Boolean = true
gitignore.isAllowed("foo.pdf")
// res14: Boolean = false
gitignore.isAllowed("bar.pdf")
// res15: Boolean = false
gitignore.isAllowed("ok.pdf")
// res16: Boolean = true
gitignore.isAllowed("foo.json")
// res17: Boolean = true
gitignore.isAllowed("bar.json")
// res18: Boolean = false
gitignore.isAllowed("ok.json")
// res19: Boolean = true
gitignore.isAllowed("target/")
// res20: Boolean = false
gitignore.isAllowed("target.json")
// res21: Boolean = true
```

Option **2** - pass list of rules to the constructor

```scala
import com.github.arturopala.gitignore._

val gitignore = GitIgnore(Seq(
    "*.txt",
    "*.pdf",
    "!ok.*",
    "bar.json ",
    "target"))
// gitignore: GitIgnore = GitIgnore(
//   gitPatterns = List("*.txt", "*.pdf", "!ok.*", "bar.json ", "target")
// )
 
gitignore.isIgnored("foo.txt")
// res23: Boolean = true
gitignore.isIgnored("bar.txt")
// res24: Boolean = true
gitignore.isIgnored("ok.txt")
// res25: Boolean = false
gitignore.isIgnored("foo.pdf")
// res26: Boolean = true
gitignore.isIgnored("bar.pdf")
// res27: Boolean = true
gitignore.isIgnored("ok.pdf")
// res28: Boolean = false
gitignore.isIgnored("foo.json")
// res29: Boolean = false
gitignore.isIgnored("bar.json")
// res30: Boolean = false
gitignore.isIgnored("ok.json")
// res31: Boolean = false
gitignore.isIgnored("target/")
// res32: Boolean = true
gitignore.isIgnored("target.json")
// res33: Boolean = false

gitignore.isAllowed("foo.txt")
// res34: Boolean = false
gitignore.isAllowed("bar.txt")
// res35: Boolean = false
gitignore.isAllowed("ok.txt")
// res36: Boolean = true
gitignore.isAllowed("foo.pdf")
// res37: Boolean = false
gitignore.isAllowed("bar.pdf")
// res38: Boolean = false
gitignore.isAllowed("ok.pdf")
// res39: Boolean = true
gitignore.isAllowed("foo.json")
// res40: Boolean = true
gitignore.isAllowed("bar.json")
// res41: Boolean = true
gitignore.isAllowed("ok.json")
// res42: Boolean = true
gitignore.isAllowed("target/")
// res43: Boolean = false
gitignore.isAllowed("target.json")
// res44: Boolean = true
```