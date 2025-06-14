import "./globals.css";

export const metadata = {
  title: "게시판",
  description: "간단한 Next.js 게시판",
};

export default function RootLayout({ children }) {
  return (
    <html lang="ko">
      <body>
        <main>
          {children}
        </main>
      </body>
    </html>
  );
}
